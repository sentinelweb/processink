package speecher.generator.movie

import processing.video.Movie
import speecher.domain.Subtitles
import java.awt.Dimension
import java.awt.geom.Rectangle2D

data class MovieState constructor(
    var movieDimension: Dimension? = null,
    var screenRect: Rectangle2D.Float? = null,
    var duration: Float? = null,
    var position: Float? = null,
    var playState: MovieContract.State = MovieContract.State.INIT,
    var subtitle: Subtitles.Subtitle? = null,
    var subStartCalled: Boolean = false
) {

    lateinit var movie: Movie

    fun isMovieInitialised() = this::movie.isInitialized

    fun isInitialised() = isMovieInitialised() && screenRect != null
}