package speecher.generator.movie

import processing.video.Movie
import speecher.domain.Subtitles
import java.io.File

class MovieContract {

    data class Config constructor(
        var playEventLatency: Float? = 0.05f
    )

    interface View {
        fun createMovie(file: File)
        fun render()
        fun hasMovie(m: Movie): Boolean
        fun movieEvent(m: Movie)
        fun cleanup()
    }

    interface Presenter {
        fun onMovieEvent()
        fun flagReady()
    }

    interface External {
        var config: Config
        var listener: Listener?
        val position: Float
        val duration: Float
        val playState: State
        val parent: Parent?
        fun openMovie(file: File)
        fun setMovieSpeed(speed: Float)
        fun play()
        fun pause()
        fun volume(vol: Float)
        fun seekTo(positionSec: Float)
        fun setSubtitle(sub: Subtitles.Subtitle)
        fun cleanup()
    }

    interface Parent {

    }

    interface Listener {
        fun onReady()
        fun onSubtitleStart(sub: Subtitles.Subtitle)
        fun onSubtitleFinished(sub: Subtitles.Subtitle)
        fun onPlaying()
    }

    interface Sketch {
        fun addView(v: View)
        fun cleanup()
    }

    enum class State { NOT_INIT, INIT, LOADED, PLAYING, PAUSED, STOPPED, SEEKING }
}