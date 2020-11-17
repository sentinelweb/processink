package speecher.generator.movie

import io.reactivex.Scheduler
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ext.getOrCreateScope
import speecher.domain.Subtitles
import speecher.generator.movie.MovieContract.State.*
import speecher.scheduler.SchedulerModule
import java.io.File

class MoviePresenter : MovieContract.Presenter, MovieContract.External {

    private val scope = this.getOrCreateScope()
    private val view: MovieContract.View = scope.get()
    private val state: MovieState = scope.get()
    private val pScheduler: Scheduler = scope.get(named(SchedulerModule.PROCESSING))

    override var listener: MovieContract.Listener? = null
    override val position: Float
        get() = state.position ?: 0f
    override val duration: Float
        get() = state.duration ?: 0f
    override val playState: MovieContract.State
        get() = state.playState

    override fun initialise() {

    }

    override fun onMovieEvent() {
        state.position
            ?.takeIf { it < state.movie.time() && state.playState != PLAYING && state.playState != PAUSED }
            ?.let { changeState(PLAYING) }

        state.position = state.movie.time()
        state.apply { println("state: $playState -> $position") }
        state.subtitle
            ?.takeIf { it.fromSec <= state.position ?: 0f && !state.subStartCalled }
            ?.let {
                state.subStartCalled = true
                changeState(PLAYING)
                listener?.onSubtitleStart(it)
            }

        state.subtitle
            ?.takeIf { it.toSec <= state.position ?: Float.MAX_VALUE }
            ?.let {
                changeState(PAUSED)
                listener?.onSubtitleFinished(it)
            }
    }

    override fun openMovie(file: File) {
        view.createMovie(file)
    }

    override fun setMovieSpeed(speed: Float) {
        state.movie.speed(speed)
    }

    override fun play() {
        state.movie.play()
    }

    override fun changeState(state1: MovieContract.State) {
        state.playState = state1
    }

    override fun pause() {
        state.movie.pause()
        changeState(PAUSED)
    }

    override fun volume(vol: Float) {
        state.movie.volume(vol)
    }

    override fun seekTo(positionSec: Float) {
        state.movie.jump(positionSec)
        changeState(SEEKING)
    }

    override fun setSubtitle(sub: Subtitles.Subtitle) {
        state.subStartCalled = false
        state.subtitle = sub
        seekTo(sub.fromSec)
    }

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