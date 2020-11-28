package speecher.generator

import speecher.generator.bank.MovieBankContract
import java.awt.Color
import java.awt.Font
import java.io.File

interface GeneratorContract {

    data class Config constructor(
        var bank: MovieBankContract.Config = MovieBankContract.Config()
    )

    interface View {
        var presenter: Presenter
        var bankView: MovieBankContract.View?

        fun run()
        fun openMovie(i: Int, file: File)
        fun setFont(fontName: String, size: Float)
        fun updateFontColor()
        fun recordNew(path: String)
        fun recordStop()
        fun cleanup()
    }

    interface Presenter {
        val subtitleToDisplay: String
        val selectedFontColor: Color?
        val selectedFont: Font?

        fun initialise()
    }
}