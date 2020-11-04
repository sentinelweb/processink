package speecher.editor.subedit

import speecher.domain.Subtitles

interface SubEditContract {

    interface Presenter {
        fun wordSelected(index: Int)
        fun sliderChanged(index: Int, time: Float)
        fun onSave(moveToNext: Boolean)
    }

    interface External {
        val listener: Listener
        fun showWindow()
        fun setReadSub(sub: Subtitles.Subtitle)
        fun setWriteSubs(subs: List<Subtitles.Subtitle>)
    }

    interface Listener {
        fun onLoopChanged(start: Float, end: Float)
        fun saveWriteSubs(subs: List<Subtitles.Subtitle>)
    }

    interface View {
        fun showWindow()
        fun setLimits(fromSec: Float, toSec: Float)
        fun setMarkers(markers: List<Float>)
        fun setWordList(words: List<String>)
    }
}