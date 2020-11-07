package speecher.editor.subedit.word_timeline

import speecher.domain.Subtitles

class WordTimelinePresenter constructor(
    private val view: WordTimelineContract.View,
    private val state: WordTimelineState
) : WordTimelineContract.Presenter, WordTimelineContract.External {

    override lateinit var listener: WordTimelineContract.External.Listener

    // region SubEditContract.External
    override fun setWords(subs: List<Subtitles.Subtitle>) {
        state.subs.clear()
        state.subs.addAll(subs)
        view.update()
    }

    override fun setLimits(fromSec: Float, toSec: Float) {
        state.limits[0] = fromSec
        state.limits[1] = toSec
        view.update()
    }

    override fun setCurrentWord(word: String) {
        state.currentWord = word
        view.update()
    }

    override fun setCurrentWordTime(fromSec: Float, toSec: Float) {
        state.currentWordLimits[0] = fromSec
        state.currentWordLimits[1] = toSec
        view.update()
    }

    override fun clearCurrentWord() {
        state.currentWord = null
        setCurrentWordTime(0f, 0f)
    }

    override fun onIndexSelected(i: Int) {
        listener.wordSelected(i, state.subs[i])
    }
    // endregion

}