package speecher.generator.bank

import processing.video.Movie
import speecher.domain.Sentence
import speecher.generator.movie.MovieContract
import java.io.File

interface MovieBankContract {

    interface Presenter {
        var listener: Listener?
        var words: Sentence?
        var looping: Boolean
        var volume: Float
        val subtitleToDisplay: String
    }

    interface External {
        fun pause()
        fun loadMovieFile(movie: File)
    }

    interface Listener {
        fun onPlayFinished()
    }

    interface View : MovieContract.Sketch {
        var active: Int
        fun render()
        fun movieEvent(m: Movie)
    }
}