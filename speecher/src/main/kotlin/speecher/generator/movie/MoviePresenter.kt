package speecher.generator.movie

import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.gstreamer.State
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ext.getOrCreateScope
import speecher.domain.Subtitles
import speecher.generator.movie.MovieContract.State.*
import speecher.scheduler.SchedulerModule
import java.io.File

class MoviePresenter(private val i: Int) : MovieContract.Presenter, MovieContract.External {

    private val scope = this.getOrCreateScope()
    private val view: MovieContract.View = scope.get()
    private val state: MovieState = scope.get()
    private val processingScheduler: Scheduler = scope.get(named(SchedulerModule.PROCESSING))
    private val playerScheduler: Scheduler = scope.get(named(SchedulerModule.PLAYER))

    override var listener: MovieContract.Listener? = null
    override val position: Float
        get() = state.position ?: 0f
    override val duration: Float
        get() = state.duration ?: 0f
    override val playState: MovieContract.State
        get() = mapState()

    private fun mapState(): MovieContract.State =
        if (state.isInitialised()) {
            if (!state.isMovieInitialised()) {
                INIT
            } else {
                if (state.movie.isSeeking() || state.seeking) {
                    SEEKING
                } else {
                    when (state.movie.playerState()) {
                        State.READY -> LOADED
                        State.VOID_PENDING -> INIT
                        State.NULL -> INIT
                        State.PAUSED -> PAUSED
                        State.PLAYING -> PLAYING
                        null -> INIT
                    }
                }
            }
        } else NOT_INIT

    // region Presenter
    override fun onMovieEvent() {
        state.position = state.movie.time()
        state.apply { println("state: $playState -> $position") }
        state.subtitle
            ?.takeIf { it.fromSec <= state.position ?: 0f && !state.onSubStartCalled }
            ?.let {
                state.onSubStartCalled = true
                listener?.onSubtitleStart(it)
            }

        state.subtitle
            ?.takeIf { it.toSec <= state.position ?: 0f }
            ?.let {
                println("sub finished : pausing = ${state.subPauseOnFinish}")
                listener?.onSubtitleFinished(it)
                if (state.subPauseOnFinish) {
                    pause()
                }
                println("after subfinish pause ($i)")
            }
    }

    override fun flagReady() {
        state.ready = true
        listener?.onReady()
    }
    // endregion

    // region External
    override fun openMovie(file: File) {
        state.ready = false
        view.createMovie(file)
    }

    override fun setMovieSpeed(speed: Float) {
        state.movie.speed(speed)
    }

    override fun play() {
        Completable.fromCallable {
            state.movie.play()
            state.movie.volume(state.volume)
        }
            .subscribeOn(Schedulers.newThread())
            .subscribe({ println("Playing ($i)") }, { it.printStackTrace() })
            .also { state.disposables.add(it) }
    }

    // fixme can be thread synchronization issues when pausing all players together
    // todo try out coroutines for these
    override fun pause() {
        Completable.fromCallable {
            state.movie.pause()
        }
            .subscribeOn(Schedulers.newThread())
            .subscribe({ println("Paused ($i)") }, { it.printStackTrace() })
            .also { state.disposables.add(it) }
    }

    override fun volume(vol: Float) {
        state.volume = vol
        state.movie.volume(vol)
    }

    override fun seekTo(positionSec: Float) {
        state.seeking = true
        Single.fromCallable {
            val startTime = System.currentTimeMillis()
            state.movie.jump(positionSec)
            startTime
        }
            .subscribeOn(Schedulers.newThread())
            .subscribe({
                state.seeking = false
                println("Jump finished: ($i) t = ${System.currentTimeMillis() - it}")
            }, { it.printStackTrace() })
            .also { state.disposables.add(it) }
    }

    override fun setSubtitle(sub: Subtitles.Subtitle, pauseOnFinish: Boolean) {
        state.onSubStartCalled = false
        state.subPauseOnFinish = pauseOnFinish
        state.subtitle = sub
        seekTo(sub.fromSec)
    }

    override fun cleanup() {
        state.disposables.dispose()
        view.cleanup()
    }
    // endregion

    companion object {
        @JvmStatic
        val scopeModule = module {
            scope(named<MoviePresenter>()) {
                scoped<MovieContract.Presenter> { getSource() }
                scoped<MovieContract.View> {
                    MovieView(
                        presenter = get(),
                        state = get(),
                        p = get(),
                        sketch = get()
                    )
                }
                scoped { MovieState() }
            }
        }
    }
}