package speecher.generator.ui

import speecher.domain.Sentence
import speecher.domain.Subtitles
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
        fun moveCursor(pos: CursorPosition)
        fun sortOrder(order: SortOrder)
        fun play()
        fun pause()
        fun searchText(text: String)
        fun openSubs()
        fun deleteWord()
        fun initView()
        fun setSrtFile(file: File)
    }

    interface External {
        var playing: Boolean
        var listener: Listener
        fun setSubs(subs: Subtitles)
        fun setSrtFile(file: File)
        fun showWindow()
    }

    interface Listener {
        fun sentenceChanged(sentence: Sentence)
        fun play()
        fun pause()
    }

    enum class CursorPosition { START, LAST, NEXT, END }
    enum class SortOrder { NATURAL, A_Z, Z_A }
}