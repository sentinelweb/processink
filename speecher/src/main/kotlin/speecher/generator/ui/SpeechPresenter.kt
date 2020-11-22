package speecher.generator.ui

import io.reactivex.schedulers.Schedulers
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ext.getOrCreateScope
import speecher.di.Modules
import speecher.domain.Sentence
import speecher.domain.Subtitles
import speecher.generator.ui.SpeechContract.CursorPosition.*
import speecher.generator.ui.SpeechContract.SortOrder.*
import speecher.generator.ui.SpeechPresenter.Companion.CURSOR
import speecher.interactor.srt.SrtInteractor
import java.io.File
import java.lang.Integer.max
import javax.swing.SwingUtilities
import kotlin.math.min

fun main() {
    startKoin { modules(Modules.allModules) }
    SpeechPresenter().apply {
        listener = object : SpeechContract.Listener {
            override fun sentenceChanged(sentence: Sentence) {
                val string = sentence.words.map {
                    if (it != CURSOR) it.sub.text[0] else ""
                }
                println("listener: sentence = $string")
            }

            override fun play() {
                playing = true
            }

            override fun pause() {
                playing = false
            }

        }
        showWindow()
        SwingUtilities.invokeLater {
            setSubs(Subtitles((0..300).mapIndexed { i, e ->
                Subtitles.Subtitle(e.toFloat(), e.toFloat() + 1, listOf("subtitle $i"))
            }))
        }
    }
}

class SpeechPresenter :
    SpeechContract.Presenter,
    SpeechContract.External {

    private val scope = this.getOrCreateScope()
    private val view: SpeechContract.View = scope.get()
    private val state: SpeechState = scope.get()
    private val srtInteractor: SrtInteractor = scope.get()

    // region presenter
    override fun moveCursor(pos: SpeechContract.CursorPosition) {
        when (pos) {
            START -> state.cursorPos = 0
            LAST -> state.cursorPos = max(0, state.cursorPos - 1)
            NEXT -> state.cursorPos = min(state.wordList.size, state.cursorPos + 1)
            END -> state.cursorPos = state.wordList.size
        }
        buildSentence()
    }

    override fun sortOrder(order: SpeechContract.SortOrder) {
        state.sortOrder = order
        updateSubs()
    }

    override fun play() {
        listener.play()
    }

    override fun pause() {
        listener.pause()
    }

    override fun searchText(text: String) {
        state.searchText = text
        updateSubs()
    }

    override fun openSubs() {
        view.showOpenDialog("Open SRT", state.srtFile?.parentFile)
    }

    override fun deleteWord() {
        if (state.cursorPos < state.wordList.size) {
            state.wordList = state.wordList.toMutableList().apply { removeAt(state.cursorPos) }
            buildSentence()
        }
    }

    override fun initView() {
        buildSentence()
    }

    // endregion

    // region External
    override lateinit var listener: SpeechContract.Listener

    override var playing: Boolean = false
        get() = field
        set(value) {
            field = value
            view.setPlaying(field)
        }

    override fun setSubs(subs: Subtitles) {
        state.subs = subs
        updateSubs()
    }

    override fun setSrtFile(file: File) {
        srtOpenSingle(file)
            .subscribe(
                { println("opened SRT = $file") },
                { it.printStackTrace() }
            )
    }

    override fun loop() {
        println("loop")
    }

    override fun showWindow() {
        view.showWindow()
    }
    // endregion

    // region SubtitleChipView.Listener [sub]
    inner class SubChipListener : SubtitleChipView.Listener {
        override fun onItemClicked(sub: Subtitles.Subtitle) {
            //println("subchip.onItemClicked $sub")
            state.wordList = state.wordList.toMutableList().apply { add(state.cursorPos, Sentence.Word(sub)) }
            state.cursorPos++
            buildSentence()
        }

        override fun onPreviewClicked(sub: Subtitles.Subtitle) {
            println("subchip.onPreviewClicked $sub")
        }
    }

    private fun pushSentence() {
        listener.sentenceChanged(Sentence(state.wordList))
    }
    // endregion

    // region SubtitleChipView.Listener [word]
    inner class WordChipListener : SubtitleChipView.Listener {

        override fun onItemClicked(sub: Subtitles.Subtitle) {
            println("word.onItemClicked $sub")
        }

        override fun onPreviewClicked(sub: Subtitles.Subtitle) {
            println("word.onPreviewClicked $sub")
        }
    }
    // endregion

    private fun buildSentence() {
        view.updateSentence(
            state.wordList.toMutableList().apply { add(state.cursorPos, CURSOR) }
        )
        pushSentence()
    }

    private fun updateSubs() {
        val subs = state.searchText
            ?.let { searchText ->
                state.subs?.timedTexts?.filter { it.text[0].contains(searchText) }
            } ?: state.subs?.timedTexts

        when (state.sortOrder) {
            NATURAL -> state.subsDisplay = subs
            A_Z -> state.subsDisplay = subs?.sortedBy { it.text[0] }
            Z_A -> state.subsDisplay = subs?.sortedBy { it.text[0] }?.reversed()
        }

        view.updateSubList(state.subsDisplay ?: listOf())
    }

    private fun srtOpenSingle(file: File) =
        srtInteractor.read(file)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                state.subs = it
                updateSubs()
                state.speakString?.let { buildWordList(it) }
                state.srtFile = file
            }

    private fun buildWordList(s: String) {
        state.wordList = state.wordList.toMutableList().apply {
            val elements = s
                .split(" ")
                .map { word -> state.subs?.timedTexts?.find { it.text[0] == word } }
                .filterNotNull()
            val elements1 = elements.map { Sentence.Word(it) }
            addAll(state.cursorPos, elements1)
            state.cursorPos = elements1.size

        }
        buildSentence()
    }

    companion object {
        val CURSOR = Sentence.Word(Subtitles.Subtitle(0f, 0f, listOf("Cursor")))
        const val CHIP_SUB = "Subs"
        const val CHIP_WORD = "Sentence"

        @JvmStatic
        val scope = module {
            scope(named<SpeechPresenter>()) {
                scoped<SpeechContract.Presenter> { getSource() }
                scoped<SubtitleChipView.Listener>(named(CHIP_SUB)) { getSource<SpeechPresenter>().SubChipListener() }
                scoped<SubtitleChipView.Listener>(named(CHIP_WORD)) { getSource<SpeechPresenter>().WordChipListener() }
                scoped<SpeechContract.View> {
                    SpeechView(
                        presenter = get(),
                        timeFormatter = get(),
                        subChipListener = get(named(CHIP_SUB)),
                        wordChipListener = get(named(CHIP_WORD))
                    )
                }
                scoped { SpeechState() }
            }
        }
    }


}