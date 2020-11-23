package speecher.generator.ui

import speecher.domain.Sentence
import speecher.domain.Subtitles
import java.awt.Color
import java.awt.Font
import java.io.File

interface SpeechContract {

    interface View {
        fun showWindow()
        fun updateSentence(sentence: List<Sentence.Word>)
        fun updateSubList(subs: List<Subtitles.Subtitle>)
        fun setPlaying(isPlaying: Boolean)
        fun showOpenDialog(title: String, currentDir: File?)
    }

    interface Presenter {
        var selectedFontColor: Color?
        var selectedFont: Font?
        var volume: Float
        var playEventLatency: Float?
        fun moveCursor(pos: CursorPosition)
        fun sortOrder(order: SortOrder)
        fun play()
        fun pause()
        fun searchText(text: String)
        fun openSubs()
        fun deleteWord()
        fun initView()
        fun setSrtFile(file: File)
        fun loop(selected: Boolean)
    }

    interface External {
        val playEventLatency: Float?
        var playing: Boolean
        var looping: Boolean
        var listener: Listener
        var selectedFontColor: Color?
        var selectedFont: Font?
        var volume: Float
        fun setSubs(subs: Subtitles)
        fun setSrtFile(file: File)
        fun showWindow()
    }

    interface Listener {
        fun sentenceChanged(sentence: Sentence)
        fun play()
        fun pause()
        fun loop(l: Boolean)
        fun updateFontColor()
        fun updateFont()
        fun updateVolume()
    }

    enum class CursorPosition { START, LAST, NEXT, END }
    enum class SortOrder { NATURAL, A_Z, Z_A }
}