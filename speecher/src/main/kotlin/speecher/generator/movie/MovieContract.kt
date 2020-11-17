package speecher.generator.movie

import processing.video.Movie
import speecher.domain.Subtitles
import java.io.File

class MovieContract {

    interface View {
        fun createMovie(file: File)
        fun render()
        fun hasMovie(m: Movie): Boolean
        fun movieEvent(m: Movie)
//        fun openMovie(file: File)
//        fun setMovieSpeed(speed: Float)
//        fun play()
//        fun pause()
//        fun volume(vol: Float)
//        fun seekTo(positionSec: Float)
    }

    interface Presenter {
        fun initialise()
        fun onMovieEvent()
    }

    interface External {
        fun openMovie(file: File)
        fun setMovieSpeed(speed: Float)
        fun play()
        fun pause()
        fun volume(vol: Float)
        fun seekTo(positionSec: Float)
        fun setSubtitle(sub: Subtitles.Subtitle)
    }

    interface Listener {

    }

    interface Sketch {
        fun addView(v: View)
    }

    enum class State { INIT, LOADED, PLAYING, PAUSED, STOPPED, SEEKING }
}