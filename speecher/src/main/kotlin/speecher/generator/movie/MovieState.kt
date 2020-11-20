package speecher.generator.movie

import speecher.domain.Subtitles
import java.awt.Dimension
import java.awt.geom.Rectangle2D

data class MovieState constructor(
    var movieDimension: Dimension? = null,
    var screenRect: Rectangle2D.Float? = null,
    var duration: Float? = null,
    var position: Float? = null,
    //var playState: MovieContract.State = MovieContract.State.INIT,
    var subtitle: Subtitles.Subtitle? = null,
    var onSubStartCalled: Boolean = false,
    var subPauseOnFinish: Boolean = false,
    var seeking: Boolean = false,
    var ready: Boolean = false
) {

    lateinit var movie: MovieWrapper

    fun isMovieInitialised() = this::movie.isInitialized

    fun isInitialised() = isMovieInitialised() && screenRect != null
}