package speecher.editor.subedit

import speecher.domain.Subtitles
import speecher.editor.subedit.word_timeline.WordTimelineContract

interface SubEditContract {

    interface Presenter {
        var wordTimeline: WordTimelineContract.External

        fun wordSelected(index: Int)
        fun sliderChanged(index: Int, pos: Float)
        fun onSave(moveToNext: Boolean)
        fun adjustSliderLimit(index: Int, timeSec: Float)
        fun onWrite()
    }

    interface External {
        var listener: Listener
        fun showWindow()
        fun setReadSub(sub: Subtitles.Subtitle)
        fun setWriteSubs(subs: List<Subtitles.Subtitle>)
    }

    interface Listener {
        fun onLoopChanged(fromSec: Float, toSec: Float)
        fun saveWriteSubs(subs: List<Subtitles.Subtitle>)
        fun markDirty()
    }

    interface View {
        fun showWindow()
        fun setLimits(fromSec: String, toSec: String)
        fun setMarkers(markers: List<Float>)
        fun setWordList(words: List<String>)
        fun setTimeText(text: String)
        fun selectWord(index: Int)
    }
}