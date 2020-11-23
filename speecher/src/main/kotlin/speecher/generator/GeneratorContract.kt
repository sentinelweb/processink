package speecher.generator

import java.io.File

interface GeneratorContract {

    interface View {
        var presenter: Presenter
        var active: Int
        fun run()
        fun openMovie(i: Int, file: File)

    }

    interface Presenter {
        val subtitleToDisplay: String?

        fun initialise()
        fun onMovieEvent(index: Int, pos: Float)
    }
}