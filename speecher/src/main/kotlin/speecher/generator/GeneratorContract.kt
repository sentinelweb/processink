package speecher.generator

import java.io.File

interface GeneratorContract {

    interface View {
        fun run()
        fun openMovie(i: Int, file: File)
        fun setMovieSpeed(i: Int, speed: Float)
        fun play(i: Int)
        fun pause(i: Int)
        fun setActive(i: Int?)
        fun volume(i: Int, vol: Float)
        fun seekTo(i: Int, positionSec: Float)
    }

    interface Presenter {
        val subtitle: String?

        fun initialise()
        fun onMovieEvent(indexOf: Int, pos: Float)
    }
}