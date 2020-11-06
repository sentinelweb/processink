package speecher.editor.subedit.word_timeline

import speecher.domain.Subtitles

interface WordTimelineContract {

    interface Presenter {

    }

    interface External {
        fun setWords(subs: List<Subtitles.Subtitle>)
        fun setLimits(fromSec: Float, toSec: Float)
        fun setCurrentWord(word: String)
        fun setCurrentWordTime(fromSec: Float, toSec: Float)
    }

    interface View {
        fun update()
        val external: WordTimelineContract.External
    }
}