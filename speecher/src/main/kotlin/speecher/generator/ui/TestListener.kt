package speecher.generator.ui

import speecher.domain.Sentence
import speecher.util.format.TimeFormatter
import speecher.util.wrapper.LogWrapper
import java.io.File

class TestListener constructor(private val p: SpeechPresenter) : SpeechContract.Listener {
    private val log = LogWrapper(TimeFormatter(), "SpeechlLstener")

    override fun sentenceChanged(sentence: Sentence) {
        val string = sentence.words.map {
            if (it != SpeechPresenter.CURSOR) it.sub.text[0] else " | "
        }
        log.d("sentence = $string")
    }

    override fun play() {
        log.d("play")
        p.playing = true
    }

    override fun pause() {
        log.d("pause")
        p.playing = false
    }

    override fun loop(l: Boolean) {
        log.d("loop = $l")
    }

    override fun updateFontColor() {
        log.d("loop = ${p.selectedFontColor}")
    }

    override fun updateFont() {
        log.d("font = ${p.selectedFont}")
    }

    override fun updateVolume() {
        log.d("volume = ${p.volume}")
    }

    override fun loadMovieFile(movie: File) {
        log.d("loadMovieFile = $movie")
    }
}