package speecher.generator.ui

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
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
import speecher.generator.ui.SpeechContract.MetaKey
import speecher.generator.ui.SpeechContract.WordParamType.*
import speecher.generator.ui.sentence_list.SentenceListContract
import speecher.interactor.sentence.SentencesInteractor
import speecher.interactor.srt.SrtInteractor
import speecher.scheduler.SchedulerModule
import speecher.util.format.FilenameFormatter
import speecher.util.format.FilenameFormatter.Companion.DEF_SENTENCE_EXT
import speecher.util.format.FilenameFormatter.Companion.DEF_WORDS_SRT_EXT
import speecher.util.format.TimeFormatter
import speecher.util.wrapper.LogWrapper
import java.awt.Color
import java.awt.Font
import java.io.File
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.swing.SwingUtilities

fun main() {
    startKoin { modules(Modules.allModules) }
    SpeechPresenter(get().get()).apply {
        listener = TestListener(this)
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
    SpeechContract.External, SentenceListContract.Listener {

    private val scope = this.getOrCreateScope()
    private val view: SpeechContract.View = scope.get()
    private var state: SpeechState = scope.get()
    private val srtInteractor: SrtInteractor = scope.get()
    private val speechStateMapper: SpeechStateMapper = scope.get()
    private val swingScheduler: Scheduler = scope.get(named(SchedulerModule.SWING))
    private val filenameFormatter: FilenameFormatter = scope.get()
    private val sentencesUi: SentenceListContract.External = scope.get()
    private val timeFormatter: TimeFormatter = scope.get()
    private val sentencesInteractor: SentencesInteractor = scope.get()

    private val disposables: CompositeDisposable = CompositeDisposable()
    private var statusDisposable: Disposable = Disposables.disposed()

    init {
        log.tag(this)
        sentencesUi.listener = this
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

    override fun moveCursor(op: SpeechContract.CursorPosition) {
        speechStateMapper.cursorOperaation(op, state)
        buildSentenceWithCursor()
    }

    override fun sortOrder(order: SpeechContract.SortOrder) {
        state.sortOrder = order
        updateWordList()
    }

    override fun play() {
        listener.play()
    }

    override fun pause() {
        listener.pause()
    }

    override fun searchText(text: String) {
        state.searchText = text
        updateWordList()
    }

    override fun openWords() {
        view.showOpenDialog("Open SRT", state.srtWordFile?.parentFile) {
            setWordsFile(it)
        }
    }

    override fun reloadWords() {
        state.srtWordFile?.apply { setWordsFile(this) }
            ?: run {
                setStatus("No srt loaded ... choose one?")
                openWords()
            }
    }

    override fun deleteWord() {
        if (state.cursorPos < state.wordSentence.size) {
            state.wordSentence = state.wordSentence.toMutableList().apply { removeAt(state.cursorPos) }
            buildSentenceWithCursor()
        }
    }

    override fun backSpace() {
        if (state.cursorPos > 0) {
            state.wordSentence = state.wordSentence.toMutableList().apply { removeAt(state.cursorPos - 1) }
            state.cursorPos--
            buildSentenceWithCursor()
        }
    }

    override fun initView() {
        buildSentenceWithCursor()
        sentencesUi.showWindow()
    }

    override fun openMovie() {
        view.showOpenDialog("Open Movie", state.srtWordFile?.parentFile) {
            if (it.exists()) {
                newSentence()
                sentencesUi.setList(mapOf())
                state.movieFile = it
                state.movieFile?.apply { listener.loadMovieFile(this) }
                val wordsFile = filenameFormatter.movieToWords(it)
                if (wordsFile.exists()) {
                    setWordsFile(wordsFile)
                } else state.srtWordFile = null
                val sentencesFile = filenameFormatter.movieToSentence(it)
                if (sentencesFile.exists()) {
                    state.sentencesFile = sentencesFile
                    openSentencesFile(sentencesFile)
                } else state.sentencesFile = null
            }
        }
    }

    // region Sentence
    override fun sentenceId(text: String) {
        state.currentSentenceId = text
    }

    override fun openSentences() {
        view.showOpenDialog("Open Sentences file", state.srtWordFile?.parentFile) {
            openSentencesFile(it)
        }
    }

    private fun openSentencesFile(file: File) {
        sentencesOpenSingle(file)
            .subscribe(
                {
                    "Opened Sentences = ${file.name}".also {
                        println(it)
                        setStatus(it)
                    }
                },
                { it.printStackTrace() }
            ).also { disposables.add(it) }
    }

    override fun saveSentences(saveAs: Boolean) {
        if (!saveAs && state.sentencesFile != null) {
            state.sentencesFile?.let { saveSentencesFile(it) }
        } else {
            val let = state.srtWordFile?.let { filenameFormatter.wordsToSentence(it) }
            view.showSaveDialog("Save Sentences file", state.sentencesFile ?: let) {
                saveSentencesFile(it)
            }
        }
    }

    private fun saveSentencesFile(file: File) {
        sentencesInteractor.saveFile(file, speechStateMapper.mapSentenceData(state, sentencesUi.getList()))
            .doOnComplete { state.sentencesFile = file }
            .subscribeOn(Schedulers.io())
            .observeOn(swingScheduler)
            .subscribe(
                {
                    "Saved Sentences = ${file.name}".also {
                        println(it)
                        setStatus(it)
                    }
                },
                { it.printStackTrace() }
            ).also { disposables.add(it) }
    }

    override fun showSentences() {
        sentencesUi.showWindow()
    }

    override fun newSentence() {
        speechStateMapper.newSentence(state)
        view.setSentenceId(state.currentSentenceId)
        buildSentenceWithCursor()
    }

    override fun commitSentence() {
        state.currentSentenceId?.let {
            sentencesUi.putSentence(it, Sentence(state.wordSentence))
        } ?: run {
            setStatus("Please set an id")
        }
    }
    // endregion sentence

    // region Clipboard
    override fun cut() {
        speechStateMapper.wordSelection(state)?.apply {
            state.clipboard = values.toList()
            state.wordSentence = state.wordSentence.toMutableList().apply { removeAll(values) }
            state.cursorPos -= keys.count { it < state.cursorPos }
            buildSentenceWithCursor()
        }
    }

    override fun copy() {
        speechStateMapper.wordSelection(state)?.apply {
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
    // endregion clipboard

    // endregion Presenter

    // region SentenceListContract.Listener
    override fun onItemSelected(key: String, sentence: Sentence) {
        println("sentence selected : $key -> ${sentence.words.map { it.sub.text[0] }.joinToString(" ")}")
        state.apply {
            wordSentence = sentence.words
            cursorPos = sentence.words.size
            editingWord = null
            wordSelection = mutableMapOf()
            currentSentenceId = key
        }
        buildSentenceWithCursor()
        view.setSentenceId(state.currentSentenceId)
    }
    // endregion SentenceListContract.Listener

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
        updateWordList()
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

    override fun setStatus(status: String) {
        statusDisposable.dispose()
        val localTime = timeFormatter.formatTime(LocalTime.now())
        view.setStatus("[$localTime] $status")
        Single.just("")
            .delay(5, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(swingScheduler)
            .subscribe({ view.clearStatus() }, { it.printStackTrace() })
            .also { statusDisposable = it }
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
            view.clearFocus()
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
                    } else if (metas.contains(MetaKey.CTRL)) {
                        if (state.editingWord == index) {
                            state.editingWord = null
                            view.selectWord(index, false)
                        } else {
                            state.editingWord?.let { view.selectWord(it, false) }
                            state.editingWord = index
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
        if (state.wordSentence.size <= state.cursorPos) {
            state.cursorPos = state.wordSentence.size
        }
        state.wordSentenceWithCursor = state.wordSentence.toMutableList().apply { add(state.cursorPos, CURSOR) }
        view.updateSentence(state.wordSentenceWithCursor)
        pushSentence()
    }

    private fun pushSentence() {
        listener.sentenceChanged(Sentence(state.wordSentence))
    }

    private fun updateWordList() {
        speechStateMapper.updateWords(state)
        view.updateSubList(state.wordsDisplay ?: listOf())
    }

    private fun srtOpenSingle(file: File) =
        srtInteractor.read(file)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                state.words = it
                updateWordList()
                //state.speakString?.let { buildWordList(it) }
                state.srtWordFile = file
            }

    private fun sentencesOpenSingle(file: File) =
        sentencesInteractor.openFile(file)
            .subscribeOn(Schedulers.io())
            .observeOn(swingScheduler)
            .doOnSuccess {
                state.sentencesFile = file
                sentencesUi.setList(it.sentences)
            }

    private fun initSingle(): Single<SpeechState> {
        return File(RC)
            .takeIf { it.exists() }
            ?.let {
                Single.fromCallable {
                    speechStateMapper.deserializeSpeechState(it.readText())
                }.subscribeOn(Schedulers.io())
                    .doOnSuccess {
                        state = it
                        view.restoreState(
                            state.volume,
                            state.playEventLatency,
                            state.searchText,
                            state.sortOrder,
                            state.currentSentenceId
                        )
                        listener.apply {
                            updateFont()
                            updateFontColor()
                            updateVolume()
                        }
                        updateWordList()
                        buildSentenceWithCursor()
                        view.updateMultiSelection(state.wordSelection.keys)

                    }.subscribeOn(swingScheduler)
                    .flatMap { speechState ->
                        state.sentencesFile
                            ?.takeIf { it.exists() }
                            ?.let { sentencesOpenSingle(it).map { speechState } }
                            ?: Single.just(speechState)
                    }

            }
            ?: Single.fromCallable {
                state.srtWordFile = File(DEF_WRITE_SRT_PATH)
                state.movieFile = File(DEF_MOVIE_PATH)
                state.sentencesFile = File(DEF_SENTENCES_PATH)
                state
            }
    }

    fun setWords(words: List<Sentence.Word>) {
        state.wordSentence = words
        buildSentenceWithCursor()
    }

    private fun buildWordList(s: String) {
        speechStateMapper.buildWordListFromString(state, s)
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
        internal val DEF_SENTENCES_PATH = "$DEF_BASE_PATH$DEF_SENTENCE_EXT"

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