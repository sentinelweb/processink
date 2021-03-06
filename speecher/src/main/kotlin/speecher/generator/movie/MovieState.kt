package speecher.generator.movie

import io.reactivex.disposables.CompositeDisposable
import speecher.domain.Subtitles
import java.awt.Dimension
import java.awt.geom.Rectangle2D

data class MovieState(
    var movieDimension: Dimension? = null,
    var screenRect: Rectangle2D.Float? = null,
    var duration: Float? = null,
    var position: Float? = null,
    //var playState: MovieContract.State = MovieContract.State.INIT,
    var subtitle: Subtitles.Subtitle? = null,
    var onSubStartCalled: Boolean = false,
    var subPauseOnFinish: Boolean = false,
    var seeking: Boolean = false,
    var ready: Boolean = false,
    val disposables: CompositeDisposable = CompositeDisposable(),
    var volume: Float = 0f,
    var onPlayEventCalled: Boolean = false,
    var bounds: Rectangle2D.Float? = null,
    var pauseAfterSeekComplete: Boolean = false
) {

    lateinit var movie: MovieWrapper

    fun isMovieInitialised() = this::movie.isInitialized

    fun isInitialised() = isMovieInitialised() && screenRect != null

    var startTime: Long = 0
}