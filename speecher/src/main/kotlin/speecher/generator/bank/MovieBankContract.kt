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
        val playEventLatency: Float = 0.05f,
        val playOneWordAtATime: Boolean = false,
        val wordSpaceTime: Int = 0,
        var playSpeed: Float = 1f
    )

    interface Presenter {
        var listener: Listener?
        val subtitleToDisplay: String
        fun loadMovieFile(movie: File)
    }

    interface External {
        val subtitleToDisplay: String
        var config: Config
        val playState: PlayState
        fun pause()
        fun loadMovieFile(movie: File)
        fun startPlaying()
        fun continuePlaying()
        fun cleanup()
    }

    interface Listener {
        fun onPlayFinished()
        fun onStateChanged()
    }

    interface View {
        var active: Int
        fun render()
        fun movieEvent(m: Movie)
        fun addMovieView(view: MovieContract.View)
        fun cleanup()
    }

    data class State(
        var words: Sentence? = null,
        var loadingWord: Int = -1, // currently loading word
        var activeIndex: Int = -1, // currently playing player
        //var playingWord: Int = -1, // currently playing word
        var movieToWordMap: MutableMap<Int, Sentence.Word?> = mutableMapOf(),
        var playAfterLoaded: Boolean = false
    )

    enum class PlayState { NOT_INIT, INIT, LOADING, LOADED, PLAYING, PAUSED, COMPLETE }
}