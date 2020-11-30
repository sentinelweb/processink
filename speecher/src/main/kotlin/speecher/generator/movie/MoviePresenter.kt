package speecher.generator.movie

import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import org.gstreamer.State
import org.koin.core.qualifier.named
import org.koin.ext.getOrCreateScope
import speecher.domain.Subtitles
import speecher.generator.movie.MovieContract.State.*
import speecher.scheduler.SchedulerModule
import speecher.util.wrapper.LogWrapper
import java.io.File

class MoviePresenter(
    private val index: Int,
    private val log: LogWrapper
) : MovieContract.Presenter, MovieContract.External {

    private val scope = this.getOrCreateScope()
    override val view: MovieContract.View = scope.get()
    private val state: MovieState = scope.get()
    private val processingScheduler: Scheduler = scope.get(named(SchedulerModule.PROCESSING))
    private val playerScheduler: Scheduler = scope.get(named(SchedulerModule.PLAYER))

    override var config: MovieContract.Config = MovieContract.Config()
        get() = field
        set(value) {
            field = value
            state.bounds = config.bounds

        }

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
        state.apply { log.d("state: $index $playState -> $position - ${state.subtitle?.toSec}") }
        state.subtitle
            ?.takeIf { it.fromSec <= state.position ?: 0f && !state.onSubStartCalled }
            ?.let {
                state.onSubStartCalled = true
                listener?.onSubtitleStart(it)
            }

        state.subtitle?.let {
            if (it.fromSec + (config.playEventLatency ?: 0f) <= state.position ?: 0f && !state.onPlayEventCalled) {
                listener?.onPlaying()
                state.onPlayEventCalled = true
            }
        }

        state.subtitle
            ?.takeIf { it.toSec <= state.position ?: 0f }
            ?.let {
                listener?.onSubtitleFinished(it)
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
            .subscribeOn(playerScheduler)
            .subscribe({ log.d("Playing ($index)") }, { it.printStackTrace() })
            .also { state.disposables.add(it) }
    }

    // fixme can be thread synchronization issues when pausing all players together
    // todo try out coroutines for these
    override fun pause() {
        if (!state.seeking) {
            Completable.fromCallable {
                state.movie.pause()
            }
                .subscribeOn(playerScheduler)
                .subscribe({
                    state.pauseAfterSeekComplete = false
                    log.d("Paused ($index)")
                }, { it.printStackTrace() })
                .also { state.disposables.add(it) }
        } else {
            state.pauseAfterSeekComplete = true
        }
    }

    override fun volume(vol: Float) {
        state.volume = vol
        state.movie.volume(vol)
    }

    override fun seekTo(positionSec: Float) {
        state.seeking = true
        log.d("Jump start: ($index)}")
        Single.fromCallable {
            val startTime = System.currentTimeMillis()
            state.movie.jump(positionSec)
            startTime
        }
            .subscribeOn(playerScheduler)
            .subscribe({
                state.seeking = false
                log.d("Jump finished: ($index) t = ${System.currentTimeMillis() - it}")
                if (state.pauseAfterSeekComplete) {
                    pause()
                }
            }, { it.printStackTrace() })
            .also { state.disposables.add(it) }
    }

    override fun setSubtitle(sub: Subtitles.Subtitle) {
        state.onSubStartCalled = false
        state.onPlayEventCalled = false
        state.subtitle = sub
        seekTo(sub.fromSec)
    }

    override fun getText(): String? = state.subtitle?.text?.get(0)


    override fun cleanup() {
        state.disposables.dispose() // todo cleanup when idle - accumulates subs
        view.cleanup()
    }
    // endregion

}