package speecher.editor.subedit

import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ext.getOrCreateScope
import speecher.domain.Subtitles
import speecher.editor.subedit.word_timeline.WordTimelineContract
import speecher.util.format.TimeFormatter

class SubEditPresenter : SubEditContract.Presenter, SubEditContract.External {
    private val scope = this.getOrCreateScope()
    private val view: SubEditContract.View = scope.get()
    private val state: SubEditState = scope.get()
    private val timeFormatter: TimeFormatter = scope.get()

    override lateinit var wordTimeline: WordTimelineContract.External

    private val selectedWord: String?
        get() = if (state.readWordSelected in (0..state.readWordList.size - 1)) {
            state.readWordList[state.readWordSelected]
        } else null

    private val sliderRange: Float
        get() = (state.sliderLimits[1] - state.sliderLimits[0])

    // region SubEditContract.External
    override lateinit var listener: SubEditContract.Listener

    override fun showWindow() {
        view.showWindow()
    }

    // fixme note writesubs is cleared here which might be ok
    override fun setReadSub(sub: Subtitles.Subtitle) {
        if (state.writeSubs.size > 0) {
            listener.saveWriteSubs(state.writeSubs)
        }
        state.readSub = sub
        state.readWordList.clear()
        state.readWordList.addAll(sub.text.map { it.split(" ") }.flatten())
        view.setWordList(state.readWordList)
        state.sliderLimits[0] = sub.fromSec
        state.sliderLimits[1] = sub.toSec
        state.sliderTimes[0] = sub.fromSec
        state.sliderTimes[1] = sub.toSec
        state.readWordSelected = -1
        state.writeSubs.clear()
        state.readToWriteMap.clear()
        updateSliderLimitTexts()
        updateSliderPositions()
        updateTimeTexts()
    }

    override fun setWriteSubs(subs: List<Subtitles.Subtitle>) {

    }

    // endregion

    // region SubEditContract.Presenter
    override fun wordSelected(index: Int) {
        selectWord(index)
        if (state.writeWordSelected == -1) {
            // todo make a guess
        } else {
            state.sliderTimes[0] = state.writeSubs[state.writeWordSelected].fromSec
            state.sliderTimes[1] = state.writeSubs[state.writeWordSelected].toSec
            updateSliderPositions()
            updateTimeTexts()
            selectedWord?.apply { wordTimeline.setCurrentWord(this) }
        }
        listener.onLoopChanged(state.sliderTimes[0], state.sliderTimes[1])
        println("wordSelected($index) --> $selectedWord")
    }

    override fun sliderChanged(index: Int, pos: Float) {
        println("sliderChanged($index, $pos)")
        state.sliderPositions[index] = pos
        updateTimeTexts()
        listener.onLoopChanged(state.sliderTimes[0], state.sliderTimes[1])
    }

    override fun onWrite() {
        listener.saveWriteSubs(state.writeSubs)
    }

    override fun adjustSliderLimit(index: Int, timeSec: Float) {
        if (state.sliderLimits[index] + timeSec > 0
            && !((index == 0 && timeSec > 0 && state.sliderLimits[0] + timeSec > state.sliderTimes[0])
                    || (index == 1 && timeSec < 0 && state.sliderLimits[1] + timeSec < state.sliderTimes[1]))
        ) {
            state.sliderLimits[index] += timeSec
            updateSliderLimitTexts()
            updateSliderPositions()
            updateTimeTexts() // should NOT change the values
            listener.onLoopChanged(state.sliderTimes[0], state.sliderTimes[1])
        }
    }

    override fun onSave(moveToNext: Boolean) {
        println("onSave(moveToNext: $moveToNext)")
        selectedWord?.let { word ->
            val element = Subtitles.Subtitle(state.sliderTimes[0], state.sliderTimes[1], listOf(word))
            if (state.writeWordSelected != -1) {
                state.writeSubs.set(state.writeWordSelected, element)
            } else {
                state.writeSubs.add(element)
                state.readToWriteMap[state.readWordSelected] = state.writeSubs.size - 1
            }
        }
        wordTimeline.setWords(state.writeSubs)
        if (moveToNext && state.readWordSelected < state.readWordList.size - 2) {
            state.readWordSelected++
            selectWord(state.readWordSelected)
            if (state.writeWordSelected == -1) {
                // basic move forward (same length as last word)
                val dist = state.sliderTimes[1] - state.sliderTimes[0]
                state.sliderTimes[0] = state.sliderTimes[1]
                state.sliderTimes[1] += dist

            } else {
                state.sliderTimes[0] = state.writeSubs[state.writeWordSelected].fromSec
                state.sliderTimes[1] = state.writeSubs[state.writeWordSelected].toSec
            }
            updateSliderPositions()
            updateTimeTexts()
            view.selectWord(state.readWordSelected)
            listener.onLoopChanged(state.sliderTimes[0], state.sliderTimes[1])
        }
    }
    // endregion

    private fun selectWord(index: Int) {
        state.readWordSelected = index
        state.writeWordSelected = state.readToWriteMap[state.readWordSelected] ?: -1
    }

    private fun updateSliderLimitTexts() {
        view.setLimits(timeFormatter.formatTime(state.sliderLimits[0]), timeFormatter.formatTime(state.sliderLimits[1]))
        wordTimeline.setLimits(state.sliderLimits[0], state.sliderLimits[1])
    }

    private fun updateSliderPositions() {
        (0..state.sliderPositions.size - 1).forEach { i ->
            state.sliderPositions[i] =
                (state.sliderTimes[i] - state.sliderLimits[0]) / sliderRange
        }
        println("sliderPositions(0,1) = ${state.sliderPositions[0]} , ${state.sliderPositions[1]}")
        view.setMarkers(state.sliderPositions.asList())
    }

    private fun updateTimeTexts() {
        (0..state.sliderTimes.size - 1).forEach { i ->
            state.sliderTimes[i] =
                state.sliderLimits[0] + (sliderRange * state.sliderPositions[i])
        }
        view.setTimeText("${timeFormatter.formatTime(state.sliderTimes[0])} --> ${timeFormatter.formatTime(state.sliderTimes[1])}")
    }

    companion object {
        @JvmStatic
        val scope = module {
            scope(named<SubEditPresenter>()) {
                scoped<SubEditContract.View> { SubEditView(get()) }
                scoped { SubEditState() }
            }
        }
    }
}