package speecher.generator.ui

import speecher.domain.Sentence
import speecher.domain.Subtitles

interface SpeechContract {

    interface View {
        fun showWindow()
        fun updateSentence(sentence: List<Sentence.Word>)
        fun updateSubList(subs: List<Subtitles.Subtitle>)
        fun setPlaying(isPlaying: Boolean)
    }

    interface Presenter {
        fun showWindow()
        fun moveCursor(pos: CursorPosition)
        fun sortOrder(order: SortOrder)
        fun play()
        fun pause()
        fun searchText(text: String)
        fun openSubs()
        fun deleteWord()
        fun initView()
    }

    interface External {
        var playing: Boolean
        var listener: Listener
        fun setSubs(subs: List<Subtitles.Subtitle>)
    }

    interface Listener {
        fun sentenceChanged(sentence: Sentence)
        fun play()
        fun pause()
    }

    enum class CursorPosition { START, LAST, NEXT, END }
    enum class SortOrder { NATURAL, A_Z, Z_A }
}