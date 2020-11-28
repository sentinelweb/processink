package speecher.generator.bank

import processing.video.Movie
import speecher.domain.Sentence
import speecher.generator.movie.MovieContract
import java.io.File

interface MovieBankContract {

    data class Config constructor(
        val words: Sentence? = null,
        val looping: Boolean = false,
        val volume: Float = 1f,
        val playEventLatency: Float = 0.05f
    )

    interface Presenter {
        var listener: Listener?
        val subtitleToDisplay: String
        fun loadMovieFile(movie: File)
    }

    interface External {
        val subtitleToDisplay: String
        var config: Config
        fun pause()
        fun loadMovieFile(movie: File)
        fun startPlaying()
        fun cleanup()
    }

    interface Listener {
        fun onPlayFinished()
    }

    interface View {
        var active: Int
        fun render()
        fun movieEvent(m: Movie)
        fun addMovieView(view: MovieContract.View)
        fun cleanup()
    }

    data class State constructor(
        var words: Sentence? = null,
        var loadingWord: Int = -1, // currently loading word
        var activeIndex: Int = -1,// currently playing player
        var playingWord: Int = -1,// currently playing word
        var movieToWordMap: MutableMap<Int, Sentence.Word?> = mutableMapOf()
    )
}