package speecher.editor.subedit

import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ext.getOrCreateScope
import speecher.domain.Subtitles
import speecher.editor.subedit.word_timeline.WordTimelineContract
import speecher.util.format.TimeFormatter
import speecher.util.subs.SubFinder
import java.lang.Float.min

class SubEditPresenter : SubEditContract.Presenter, SubEditContract.External {
    private val scope = this.getOrCreateScope()
    private val view: SubEditContract.View = scope.get()
    private val state: SubEditState = scope.get()
    private val timeFormatter: TimeFormatter = scope.get()
    private val subFinder: SubFinder = scope.get()

    private val selectedWord: String?
        get() = if (state.readWordSelected in (0..state.readWordList.size - 1)) {
            state.readWordList[state.readWordSelected]
        } else null

    private val sliderRange: Float
        get() = (state.sliderLimits[1] - state.sliderLimits[0])

    init {

    }

    // region SubEditContract.External
    override lateinit var listener: SubEditContract.Listener

    override fun showWindow() {
        view.showWindow()
    }

    // fixme note writesubs is cleared here which might be ok
    override fun setReadSub(readSub: Subtitles.Subtitle) {
        if (state.writeSubs.size > 0) {
            listener.saveWriteSubs(state.writeSubs)
        }
        state.readSub = readSub
        state.readWordList.clear()
        state.readWordList.addAll(readSub.text.map { it.split(" ") }.flatten())
        view.setWordList(state.readWordList)
        state.sliderLimits[0] = readSub.fromSec
        state.sliderLimits[1] = readSub.toSec
        state.sliderTimes[0] = readSub.fromSec
        state.sliderTimes[1] = readSub.toSec
        state.readWordSelected = -1
        state.writeSubs.clear()
        state.readToWriteMap.clear()
        updateSliderLimitTexts()
        updateSliderPositions()
        updateTimeTexts()
        view.wordTimelineExt.setLimits(readSub.fromSec, readSub.toSec)
        view.wordTimelineExt.clearCurrentWord()
    }

    override fun setWriteSubs(subs: List<Subtitles.Subtitle>) {
        state.writeSubs.clear()
        state.writeSubs.addAll(subs)
        // println("WriteSubs: ${subs.map { it.text[0] }}")
        view.wordTimelineExt.setWords(state.writeSubs)
        state.readToWriteMap.clear()
        // state.readToWriteMap.putAll(subFinder.buildMap(state.readWordList, subs))
        state.readToWriteMap.putAll(subFinder.buildMapSimple(state.readWordList, subs))

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

        }
        updateWordTimelineCurrentWord()
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
        listener.markDirty()
        listener.saveWriteSubs(state.writeSubs)
    }

    override fun onInitialised() {
        registerWordTimelineListener()
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
        listener.markDirty()
        view.wordTimelineExt.setWords(state.writeSubs)
        if (moveToNext && state.readWordSelected <= state.readWordList.size - 2) {
            state.readWordSelected++
            selectWord(state.readWordSelected)
            if (state.writeWordSelected == -1) {
                // basic move forward (same length as last word)
                val dist = state.sliderTimes[1] - state.sliderTimes[0]
                state.sliderTimes[0] = state.sliderTimes[1]
                state.sliderTimes[1] += dist
                state.sliderTimes[1] = min(state.sliderTimes[1], state.sliderLimits[1])
            } else {
                state.sliderTimes[0] = state.writeSubs[state.writeWordSelected].fromSec
                state.sliderTimes[1] = state.writeSubs[state.writeWordSelected].toSec
            }
            updateSliderPositions()
            updateTimeTexts()
            updateWordTimelineCurrentWord()
            view.selectWord(state.readWordSelected)
            listener.onLoopChanged(state.sliderTimes[0], state.sliderTimes[1])
        }
    }

    private fun selectWord(index: Int) {
        state.readWordSelected = index
        state.writeWordSelected = state.readToWriteMap[state.readWordSelected] ?: -1

    }

    private fun updateWordTimelineCurrentWord() {
        selectedWord?.let {
            view.wordTimelineExt.setCurrentWord(it)
            if (state.writeWordSelected > -1) {
                val write = state.writeSubs[state.writeWordSelected]
                view.wordTimelineExt.setCurrentWordTime(write.fromSec, write.toSec)
            }
        }
    }

    private fun updateSliderLimitTexts() {
        view.setLimits(timeFormatter.formatTime(state.sliderLimits[0]), timeFormatter.formatTime(state.sliderLimits[1]))
        view.wordTimelineExt.setLimits(state.sliderLimits[0], state.sliderLimits[1])
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
        view.wordTimelineExt.setCurrentWordTime(state.sliderTimes[0], state.sliderTimes[1])
        view.setTimeText("${timeFormatter.formatTime(state.sliderTimes[0])} --> ${timeFormatter.formatTime(state.sliderTimes[1])}")
    }

    // endregion
    private fun registerWordTimelineListener() {
        view.wordTimelineExt.listener = object : WordTimelineContract.External.Listener {
            override fun wordSelected(i: Int, word: Subtitles.Subtitle) {
                if (state.readWordSelected > -1 && state.readWordSelected == i) {
                    selectedWord?.apply {
                        if (this == state.writeSubs[i].text[0] && this == word.text[0]) {
                            state.readToWriteMap.put(state.readWordSelected, i)
                            state.writeWordSelected = i
                            state.sliderTimes[0] = state.writeSubs[state.writeWordSelected].fromSec
                            state.sliderTimes[1] = state.writeSubs[state.writeWordSelected].toSec
                            updateSliderPositions()
                            updateTimeTexts()
                            view.selectWord(state.readWordSelected)
                            listener.onLoopChanged(state.sliderTimes[0], state.sliderTimes[1])
                        } else {
                            println("Error: No match read:$this -> ${state.writeSubs[i].text[0]} -> ${word.text[0]}")
                        }
                    }
                }
            }
        }
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
