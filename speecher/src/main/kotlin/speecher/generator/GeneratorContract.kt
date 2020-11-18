package speecher.generator

import java.io.File

interface GeneratorContract {

    interface View {
        var presenter: GeneratorContract.Presenter
        fun run()
        fun openMovie(i: Int, file: File)
    }

    interface Presenter {
        val subtitle: String?

        fun initialise()
        fun onMovieEvent(index: Int, pos: Float)
    }
}