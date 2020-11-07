package speecher.editor.subedit.word_timeline

import speecher.domain.Subtitles

interface WordTimelineContract {

    interface Presenter {
        fun onIndexSelected(i: Int)
    }

    interface External {
        var listener: Listener
        fun setWords(subs: List<Subtitles.Subtitle>)
        fun setLimits(fromSec: Float, toSec: Float)
        fun setCurrentWord(word: String)
        fun setCurrentWordTime(fromSec: Float, toSec: Float)
        fun clearCurrentWord()

        interface Listener {
            fun wordSelected(i: Int, word: Subtitles.Subtitle)
        }
    }

    interface View {
        val external: External
        fun update()


    }


}