package speecher.editor.subedit

import speecher.domain.Subtitles
import speecher.editor.subedit.word_timeline.WordTimelineContract

interface SubEditContract {

    interface Presenter {
        fun wordSelected(index: Int)
        fun sliderChanged(index: Int, pos: Float)
        fun onSave(moveToNext: Boolean)
        fun adjustSliderLimit(index: Int, timeSec: Float)
        fun onWrite()
        fun onInitialised()
    }

    interface External {
        var listener: Listener
        fun showWindow()
        fun setReadSub(readSub: Subtitles.Subtitle)
        fun setWriteSubs(subs: List<Subtitles.Subtitle>)
    }

    interface Listener {
        fun onLoopChanged(fromSec: Float, toSec: Float)
        fun saveWriteSubs(subs: List<Subtitles.Subtitle>)
        fun markDirty()
    }

    interface View {
        val wordTimelineExt: WordTimelineContract.External
        fun showWindow()
        fun setLimits(fromSec: String, toSec: String)
        fun setMarkers(markers: List<Float>)
        fun setWordList(words: List<String>)
        fun setTimeText(text: String)
        fun selectWord(index: Int)

    }
}