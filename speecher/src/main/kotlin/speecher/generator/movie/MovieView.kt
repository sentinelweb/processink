package speecher.generator.movie

import org.gstreamer.State
import processing.core.PApplet
import processing.video.Movie
import java.awt.Dimension
import java.awt.geom.Rectangle2D
import java.io.File

class MovieView constructor(
    private val p: PApplet,
    private val sketch: MovieContract.Sketch,
    private val presenter: MovieContract.Presenter,
    private val state: MovieState
) : MovieContract.View {

    override fun createMovie(file: File) {
        state.movie = MovieWrapper(p, file.absolutePath)
        state.movie.play()
        sketch.addView(this)
    }

    override fun render() {
        if (state.isMovieInitialised() && !state.isInitialised()) {
            state.movie.read()
            initialise()
        }
        if (state.isInitialised() && state.movie.playerState() == State.PLAYING) {
            state.screenRect?.apply {
                p.image(state.movie, x, y, width, height)
            }
        }
    }

    override fun cleanup() {
        if (state.isMovieInitialised()) {
            state.movie.stop()
            state.movie.dispose()
        }
    }

    override fun hasMovie(m: Movie) =
        if (state.isMovieInitialised()) {
            state.movie == m
        } else false

    override fun movieEvent(m: Movie) {
        if (!state.isInitialised()) {
            initialise()
        }
        presenter.onMovieEvent()
    }

    private fun initialise() {
        if (state.movie.width > 0 && state.movie.height > 0) {
            state.movieDimension = Dimension(state.movie.width, state.movie.height)
            val movieAspect = state.movieDimension!!.width / state.movieDimension!!.height.toFloat()
            val screenAspect = p.width / p.height.toFloat()
            state.screenRect = Rectangle2D.Float(
                0f,
                ((p.height - p.height * screenAspect / movieAspect) / 2f),
                p.width.toFloat(),
                (p.width / movieAspect)
            )
            state.duration = state.movie.duration()
            presenter.flagReady()
        }
    }
}