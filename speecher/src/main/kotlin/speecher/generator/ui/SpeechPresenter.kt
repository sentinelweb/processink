package speecher.generator.ui

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.context.KoinContextHandler.get
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

import org.koin.ext.getOrCreateScope
import speecher.di.Modules
import speecher.domain.Sentence
import speecher.domain.Subtitles
import speecher.generator.GeneratorPresenter
import speecher.generator.ui.SpeechContract.CursorPosition.*
import speecher.generator.ui.SpeechContract.MetaKey
import speecher.generator.ui.SpeechContract.SortOrder.*
import speecher.generator.ui.SpeechContract.WordParamType.*
import speecher.generator.ui.SpeechPresenter.Companion.CURSOR
import speecher.generator.ui.sentence_list.SentenceListContract
import speecher.interactor.srt.SrtInteractor
import speecher.scheduler.SchedulerModule
import speecher.util.format.FilenameFormatter
import speecher.util.format.FilenameFormatter.Companion.DEF_WORDS_SRT_EXT
import speecher.util.format.TimeFormatter
import speecher.util.wrapper.LogWrapper
import java.awt.Color
import java.awt.Font
import java.io.File
import java.lang.Integer.max
import javax.swing.SwingUtilities
import kotlin.math.min

fun main() {
    startKoin { modules(Modules.allModules) }
    SpeechPresenter(get().get()).apply {
        listener = object : SpeechContract.Listener {

            private val log = LogWrapper(TimeFormatter(), "SpeechlLstener")

            override fun sentenceChanged(sentence: Sentence) {
                val string = sentence.words.map {
                    if (it != CURSOR) it.sub.text[0] else " | "
                }
                log.d("sentence = $string")
            }

            override fun play() {
                log.d("play")
                playing = true
            }

            override fun pause() {
                log.d("pause")
                playing = false
            }

            override fun loop(l: Boolean) {
                log.d("loop = $l")
            }

            override fun updateFontColor() {
                log.d("loop = $selectedFontColor")
            }

            override fun updateFont() {
                log.d("font = $selectedFont")
            }

            override fun updateVolume() {
                log.d("volume = $volume")
            }

            override fun loadMovieFile(movie: File) {
                log.d("loadMovieFile = $movie")
            }

        }
        showWindow()
        SwingUtilities.invokeLater {
            val subs = Subtitles((0..300).mapIndexed { i, e ->
                Subtitles.Subtitle(e.toFloat(), e.toFloat() + 1, listOf("subtitle $i"))
            })
            setSubs(subs)
            setWords(subs.timedTexts.subList(0, 20).map { Sentence.Word(it) })
        }
    }
}

class SpeechPresenter constructor(
    private val log: LogWrapper
) : SpeechContract.Presenter,
    SpeechContract.External {

    private val scope = this.getOrCreateScope()
    private val view: SpeechContract.View = scope.get()
    private var state: SpeechState = scope.get()
    private val srtInteractor: SrtInteractor = scope.get()
    private val speechStateMapper: SpeechStateMapper = scope.get()
    private val swingScheduler: Scheduler = scope.get(named(SchedulerModule.SWING))
    private val filenameFormatter: FilenameFormatter = scope.get()
    private val sentences: SentenceListContract.External = scope.get()

    val disposables: CompositeDisposable = CompositeDisposable()

    init {
        log.tag(this)
    }

    // region presenter
    override var selectedFontColor: Color?
        get() = state.selectedFontColor
        set(value) {
            state.selectedFontColor = value
            listener.updateFontColor()
        }

    override var selectedFont: Font?
        get() = state.selectedFont
        set(value) {
            state.selectedFont = value
            listener.updateFont()
        }

    override var volume: Float
        get() = state.volume
        set(value) {
            state.volume = value
            listener.updateVolume()
        }

    override var playEventLatency: Float?
        get() = state.playEventLatency
        set(value) {
            state.playEventLatency = value
            log.d(" = $value s")
        }

    override fun moveCursor(pos: SpeechContract.CursorPosition) {
        when (pos) {
            START -> state.cursorPos = 0
            LAST -> state.cursorPos = max(0, state.cursorPos - 1)
            NEXT -> state.cursorPos = min(state.wordSentence.size, state.cursorPos + 1)
            END -> state.cursorPos = state.wordSentence.size
        }
        buildSentenceWithCursor()
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

    override fun openWords() {
        view.showOpenDialog("Open SRT", state.srtWordFile?.parentFile) {
            setWordsFile(it)
        }
    }

    override fun deleteWord() {
        if (state.cursorPos < state.wordSentence.size) {
            state.wordSentence = state.wordSentence.toMutableList().apply { removeAt(state.cursorPos) }
            buildSentenceWithCursor()
        }
    }

    override fun initView() {
        buildSentenceWithCursor()
    }

    override fun openMovie() {
        // todo test later
        view.showOpenDialog("Open SRT", state.srtWordFile?.parentFile) {
            state.movieFile = it
            state.movieFile?.apply { listener.loadMovieFile(this) }
            val wordsFile = filenameFormatter.movieToWords(it)
            if (wordsFile.exists()) {
                setWordsFile(it)
            }
        }
    }

    override fun openSentences() {
        view.showOpenDialog("Open Sentences", state.srtWordFile?.parentFile) {

        }
    }

    override fun saveSentences() {
        println("saveSentences")
    }

    override fun showSentences() {
        sentences.showWindow()
    }
    // endregion

    // region Clipboard
    override fun cut() {
        makeWordSelection()?.apply {
            state.clipboard = values.toList()
            state.wordSentence = state.wordSentence.toMutableList().apply { removeAll(values) }
            state.cursorPos -= keys.count { it < state.cursorPos }
            buildSentenceWithCursor()
        }
    }

    override fun copy() {
        makeWordSelection()?.apply {
            state.clipboard = values.toList()
        }
    }

    override fun paste() {
        state.clipboard?.let {
            state.wordSentence = state.wordSentence.toMutableList().apply { addAll(state.cursorPos, it) }
            state.cursorPos += it.size
            buildSentenceWithCursor()
        }
    }

    private fun makeWordSelection(): Map<Int, Sentence.Word>? =
        if (state.wordSelection.size > 0) {
            state.wordSelection
        } else if (state.cursorPos + 1 < state.wordSentenceWithCursor.size) {
            mapOf(state.cursorPos + 1 to state.wordSentenceWithCursor[state.cursorPos + 1])
        } else null
    // endregion

    // region External
    override lateinit var listener: SpeechContract.Listener


    override var playing: Boolean = false
        get() = field
        set(value) {
            field = value
            view.setPlaying(field)
        }

    override var looping: Boolean = false
        get() = field
        set(value) {
            field = value
        }

    override fun showWindow() {
        view.showWindow()
    }

    override fun setSubs(subs: Subtitles) {
        state.words = subs
        updateSubs()
    }

    override fun setWordsFile(file: File) {
        srtOpenSingle(file)
            .subscribe(
                { println("opened SRT = $file") },
                { it.printStackTrace() }
            ).also { disposables.add(it) }
    }

    override fun loop(selected: Boolean) {
        listener.loop(selected)
    }

    override fun initialise() {
        initSingle()
            .flatMap { srtOpenSingle(File(DEF_WRITE_SRT_PATH)) }
            .doOnSuccess {
                state.movieFile?.apply { listener.loadMovieFile(this) }
            }
            .subscribe(
                {
                    log.d("initialised state")
                }, { t -> t.printStackTrace() }
            )
            .also { disposables.add(it) }
    }

    override fun shutdown() {
        Single.just(File(RC))
            .doOnSuccess {
                it.writeText(speechStateMapper.serializeSpeechState(state))
            }
            .subscribe({
                log.d("saved state")
                System.exit(0)
            }, { it.printStackTrace() })
            .also { disposables.add(it) }
    }
    // endregion

    // region SubtitleChipView.Listener [sub]
    inner class SubChipListener : SpeechContract.SubListener {
        override fun onItemClicked(sub: Subtitles.Subtitle, metas: List<MetaKey>) {
            //println("subchip.onItemClicked $sub")
            state.wordSentence = state.wordSentence.toMutableList().apply { add(state.cursorPos, Sentence.Word(sub)) }
            state.cursorPos++
            buildSentenceWithCursor()
        }

        override fun onPreviewClicked(sub: Subtitles.Subtitle) {
            log.d("subchip.onPreviewClicked $sub")
        }
    }

    // endregion

    // region SubtitleChipView.Listener [word]
    inner class WordChipListener : SpeechContract.WordListener {

        override fun onItemClicked(index: Int, metas: List<MetaKey>) {
            log.d("word.onItemClicked $index")
            val word = state.wordSentenceWithCursor[index]
            if (word != CURSOR) {
                if (metas.size == 0) {
                    state.cursorPos = if (state.cursorPos < index) index - 1 else index
                    buildSentenceWithCursor()
                } else {// selection tools
                    if (metas.contains(MetaKey.META)) {
                        if (state.wordSelection.containsKey(index)) {
                            state.wordSelection.remove(index)
                        } else {
                            state.wordSelection.put(index, word)
                        }
                        view.updateMultiSelection(state.wordSelection.keys)
                    } else {
                        if (state.selectedWord == index) {
                            state.selectedWord = null
                            view.selectWord(index, false)
                        } else {
                            state.selectedWord?.let { view.selectWord(it, false) }
                            state.selectedWord = index
                            view.selectWord(index, true)
                        }
                    }
                }
            }
        }

        override fun onPreviewClicked(index: Int) {
            log.d("word.onPreviewClicked $index")
        }

        override fun changed(index: Int, type: SpeechContract.WordParamType, value: Float) {
            log.d("word.changed $index $type $value")
            val word = state.wordSentenceWithCursor[index]
            if (word != CURSOR) {
                state.wordSentence = state.wordSentenceWithCursor.toMutableList().apply {
                    when (type) {
                        BEFORE -> set(index, word.copy(spaceBefore = value))
                        AFTER -> set(index, word.copy(spaceAfter = value))
                        FROM -> set(index, word.copy(sub = word.sub.copy(fromSec = value)))
                        TO -> set(index, word.copy(sub = word.sub.copy(toSec = value)))
                        SPEED -> set(index, word.copy(speed = value))
                        VOL -> set(index, word.copy(vol = value))
                    }
                    remove(CURSOR)
                }
                pushSentence()
            }
        }

    }
    // endregion

    private fun buildSentenceWithCursor(clearSelection: Boolean = true) {
        if (clearSelection) state.wordSelection.clear()
        state.wordSentenceWithCursor = state.wordSentence.toMutableList().apply { add(state.cursorPos, CURSOR) }
        view.updateSentence(state.wordSentenceWithCursor)
        pushSentence()
    }

    private fun pushSentence() {
        listener.sentenceChanged(Sentence(state.wordSentence))
    }

    private fun updateSubs() {
        val subs = state.searchText
            ?.let { searchText ->
                state.words?.timedTexts?.filter { it.text[0].contains(searchText) }
            } ?: state.words?.timedTexts

        when (state.sortOrder) {
            NATURAL -> state.wordsDisplay = subs
            A_Z -> state.wordsDisplay = subs?.sortedBy { it.text[0] }
            Z_A -> state.wordsDisplay = subs?.sortedBy { it.text[0] }?.reversed()
        }

        view.updateSubList(state.wordsDisplay ?: listOf())
    }

    private fun srtOpenSingle(file: File) =
        srtInteractor.read(file)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                state.words = it
                updateSubs()
                //state.speakString?.let { buildWordList(it) }
                state.srtWordFile = file
            }

    private fun initSingle(): Single<out Any> {
        val rcFile = File(RC)
        val initSingle = if (rcFile.exists()) {
            Single.fromCallable {
                speechStateMapper.deserializeSpeechState(rcFile.readText())
            }.subscribeOn(Schedulers.io())
                .doOnSuccess {
                    state = it
                    view.restoreState(
                        state.volume,
                        state.playEventLatency,
                        state.searchText,
                        state.sortOrder
                    )
                    listener.apply {
                        updateFont()
                        updateFontColor()
                        updateVolume()
                    }
                    updateSubs()
                    buildSentenceWithCursor()
                    view.updateMultiSelection(state.wordSelection.keys)
                }
                .subscribeOn(swingScheduler)
        } else {
            Single.fromCallable {
                state.srtWordFile = File(DEF_WRITE_SRT_PATH)
                state.movieFile = File(DEF_MOVIE_PATH)
            }
        }
        return initSingle
    }

    fun setWords(words: List<Sentence.Word>) {
        state.wordSentence = words
        buildSentenceWithCursor()
    }

    private fun buildWordList(s: String) {
        state.wordSentence = state.wordSentence.toMutableList().apply {
            val elements = s
                .split(" ")
                .map { word -> state.words?.timedTexts?.find { it.text[0] == word } }
                .filterNotNull()
            val elements1 = elements.map { Sentence.Word(it) }
            addAll(state.cursorPos, elements1)
            state.cursorPos = elements1.size

        }
        buildSentenceWithCursor()
    }

    companion object {
        internal val CURSOR = Sentence.Word(Subtitles.Subtitle(0f, 0f, listOf("Cursor")))
        private const val CHIP_SUB = "Subs"
        private const val CHIP_WORD = "Sentence"

        internal val DEF_BASE_PATH =
            "${GeneratorPresenter.BASE}/ytcaptiondl/Boris Johnson - 3rd Margaret Thatcher Lecture (FULL)-Dzlgrnr1ZB0"
        internal val DEF_MOVIE_PATH = "$DEF_BASE_PATH.mp4"

        internal val DEF_WRITE_SRT_PATH = "$DEF_BASE_PATH$DEF_WORDS_SRT_EXT"

        internal val RC = "${System.getProperty("user.home")}/.speecherrc.json"

        @JvmStatic
        val scope = module {
            scope(named<SpeechPresenter>()) {
                scoped<SpeechContract.Presenter> { getSource() }
                scoped<SpeechContract.SubListener>(named(CHIP_SUB)) { getSource<SpeechPresenter>().SubChipListener() }
                scoped<SpeechContract.WordListener>(named(CHIP_WORD)) { getSource<SpeechPresenter>().WordChipListener() }
                scoped<SpeechContract.View> {
                    SpeechView(
                        presenter = get(),
                        timeFormatter = get(),
                        subChipListener = get(named(CHIP_SUB)),
                        wordChipListener = get(named(CHIP_WORD))
                    )
                }
                scoped { SpeechStateMapper(get()) }
                scoped { SpeechState() }
            }
        }
    }


}