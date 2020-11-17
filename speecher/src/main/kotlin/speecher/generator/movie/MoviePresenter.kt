package speecher.generator.movie

import io.reactivex.Scheduler
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ext.getOrCreateScope
import speecher.domain.Subtitles
import speecher.scheduler.SchedulerModule
import java.io.File

class MoviePresenter : MovieContract.Presenter, MovieContract.External {

    private val scope = this.getOrCreateScope()
    private val view: MovieContract.View = scope.get()
    private val state: MovieState = scope.get()
    private val pScheduler: Scheduler = scope.get(named(SchedulerModule.PROCESSING))

    override fun initialise() {

    }

    override fun onMovieEvent() {

    }

    override fun openMovie(file: File) {
        view.createMovie(file)
    }

    override fun setMovieSpeed(speed: Float) {
        state.movie.speed(speed)
    }

    override fun play() {
        state.movie.play()
        state.playState = MovieContract.State.PLAYING
    }

    override fun pause() {
        state.movie.pause()
        state.playState = MovieContract.State.PAUSED
    }

    override fun volume(vol: Float) {
        state.movie.volume(vol)
    }

    override fun seekTo(positionSec: Float) {
        state.movie.jump(positionSec)
    }

    override fun setSubtitle(sub: Subtitles.Subtitle) {

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