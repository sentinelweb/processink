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
        fun selectWord(index: Int, selected: Boolean)
        fun restoreState(
            vol: Float,
            playEventLatency: Float?,
            searchText: String?,
            sortOrder: SortOrder,
            currentSentenceId: String?
        )

        fun showOpenDialog(title: String, currentDir: File?, chosen: (f: File) -> Unit)
        fun showSaveDialog(title: String, currentDir: File?, chosen: (f: File) -> Unit)
        fun updateMultiSelection(keys: MutableSet<Int>)
        fun setStatus(status: String)
        fun clearStatus()
        fun setSentenceId(currentSentenceId: String?)
        fun clearFocus()
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
        fun openWords()
        fun deleteWord()
        fun initView()
        fun setWordsFile(file: File)
        fun loop(selected: Boolean)
        fun shutdown()
        fun openMovie()
        fun openSentences()
        fun saveSentences(saveAs: Boolean)
        fun cut()
        fun copy()
        fun paste()
        fun showSentences()
        fun newSentence()
        fun commitSentence()
        fun backSpace()
        fun sentenceId(text: String)
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
        fun setWordsFile(file: File)
        fun showWindow()
        fun shutdown()
        fun initialise()
        fun setStatus(status: String)
    }

    interface Listener {
        fun sentenceChanged(sentence: Sentence)
        fun play()
        fun pause()
        fun loop(l: Boolean)
        fun updateFontColor()
        fun updateFont()
        fun updateVolume()
        fun loadMovieFile(movie: File)
    }

    interface WordListener {
        fun changed(index: Int, type: WordParamType, value: Float)
        fun onItemClicked(index: Int, metas: List<MetaKey>)
        fun onPreviewClicked(index: Int)
    }

    interface SubListener {
        fun onItemClicked(sub: Subtitles.Subtitle, metas: List<MetaKey>)
        fun onPreviewClicked(sub: Subtitles.Subtitle)
    }

    enum class CursorPosition { START, LAST, NEXT, END }
    enum class SortOrder { NATURAL, A_Z, Z_A }
    enum class WordParamType { BEFORE, AFTER, SPEED, VOL, FROM, TO }
    enum class MetaKey { SHIFT, CTRL, ALT, META }

}