package speecher.generator

import java.awt.Color
import java.awt.Font
import java.io.File

interface GeneratorContract {

    interface View {
        var presenter: Presenter
        var active: Int
        fun run()
        fun openMovie(i: Int, file: File)

        fun setFont(fontName: String, size: Float)
        fun updateFontColor()
        fun recordNew(path: String)
        fun recordStop()
    }

    interface Presenter {
        val subtitleToDisplay: String?
        val selectedFontColor: Color?
        val selectedFont: Font?

        fun initialise()
        fun onMovieEvent(index: Int, pos: Float)
    }
}