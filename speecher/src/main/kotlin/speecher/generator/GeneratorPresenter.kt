package speecher.generator

import io.reactivex.Scheduler
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.context.KoinContextHandler.get
import org.koin.core.context.startKoin
import org.koin.core.get
import org.koin.core.qualifier.named
import org.koin.dsl.module
import processing.core.PApplet
import speecher.di.Modules
import speecher.domain.Sentence
import speecher.domain.Subtitles
import speecher.generator.movie.MovieContract
import speecher.generator.movie.MoviePresenter
import speecher.generator.ui.SpeechContract
import speecher.scheduler.SchedulerModule.PROCESSING
import speecher.scheduler.SchedulerModule.SWING
import speecher.util.wrapper.LogWrapper
import java.awt.Color
import java.awt.Font
import java.io.File

fun main() {
    startKoin {
        modules(Modules.allModules)
    }
    GeneratorPresenter(get().get())
}

class GeneratorPresenter constructor(
    private val log: LogWrapper
) : GeneratorContract.Presenter, SpeechContract.Listener, KoinComponent, MovieContract.Parent {

    private val view: GeneratorContract.View = get()
    private val state: GeneratorState = get()
    private val pScheduler: Scheduler = get(named(PROCESSING))
    private val swingScheduler: Scheduler = get(named(SWING))
    private val speechUI: SpeechContract.External = get()

    init {
        log.tag(this)
    }

    override val subtitleToDisplay: String?
        get() = state.movieToWordMap[state.activeIndex]?.sub?.text?.get(0) ?: "-"
    override val selectedFontColor: Color?
        get() = speechUI.selectedFontColor
    override val selectedFont: Font?
        get() = speechUI.selectedFont
    override val playEventLatency: Float?
        get() = speechUI.playEventLatency

    private val movies = mutableListOf<MovieContract.External>()

    private fun wordAtIndex(index: Int): Sentence.Word? = state.words?.words?.let {
        if (index < it.size) it.get(index) else null
    }

    private fun Int.wrapInc() = if (this + 1 < movies.size) this + 1 else 0

    init {
        view.presenter = this
        view.run()
        speechUI.listener = this
        speechUI.showWindow()
    }

    override fun initialise() {
        openMovieSingle(File(DEF_MOVIE_PATH))
            .doOnSuccess {
                speechUI.setSrtFile(File(DEF_WRITE_SRT_PATH))
            }
            //.observeOn(pScheduler)
            .doOnSuccess {
                state.wordIndex = -1
                state.startTime = System.currentTimeMillis()
                movies.forEachIndexed { i, movie ->
                    movie.pause()
                }
            }
            .subscribe({
                println("Opened movie file : $it")
            }, { it.printStackTrace() })
            .also { state.disposables.add(it) }
    }

    inner class MvListener(private val index: Int) : MovieContract.Listener {

        override fun onReady() {

        }

        override fun onSubtitleStart(sub: Subtitles.Subtitle) {
            //log.d("onSubtitleStart ${state.activeIndex}")
        }

        override fun onSubtitleFinished(sub: Subtitles.Subtitle) {
            log.d("onSubtitleFinished ${state.activeIndex}")
            playNext()
        }

        override fun onPlaying() {
            log.d("onPlaying($index active=${state.activeIndex})")
            if (index == state.activeIndex) {
                view.active = state.activeIndex
            }
        }

    }

    override fun onMovieEvent(index: Int, pos: Float) {
    }

    // region Movie
    private fun openMovieSingle(file: File): Single<File> {
        return Single.just(file)
            .doOnSuccess { state.movieFile = it }
            .observeOn(pScheduler)
            .doOnSuccess {
                (0..10).forEach {
                    makeMovie(it, file)
                }
            }
    }

    private fun makeMovie(i: Int, file: File) = MoviePresenter(i, log).apply {
        listener = MvListener(i)
        parent = this@GeneratorPresenter
        movies.add(this)
        openMovie(file)
        volume(0f)
//        pause()
    }
    // endregion

    // region SpeechContract.External
    override fun sentenceChanged(sentence: Sentence) {
        state.words = sentence
    }

    override fun play() {
        speechUI.playing = true
        startPlaying()
    }

    override fun pause() {
        movies.forEach { it.pause() }
        speechUI.playing = false
    }

    override fun loop(l: Boolean) {
        state.looping = l
    }

    override fun updateFontColor() {
        view.updateFontColor()
    }

    override fun updateFont() {
        updateViewFont()
    }

    override fun updateVolume() {
        state.volume = speechUI.volume
    }

    private fun updateViewFont() {
        view.setFont(selectedFont?.fontName ?: "Thonburi", selectedFont?.size?.toFloat() ?: 24f)
    }

    // endregion

    // region playback
    private fun startPlaying() {
//        File(RECORD_PATH).mkdir()
//        view.recordNew(RECORD_PATH)
        log.startTime()
        movies.forEach {
            it.volume(state.volume)
        }
        state.wordIndex = 0
        state.activeIndex = 0
        wordAtIndex(state.wordIndex)?.let {
            state.movieToWordMap[state.activeIndex] = it
            movies[state.activeIndex].setSubtitle(it.sub)
        }

        movies[state.activeIndex].play()
        (1..movies.size - 1).forEach { i ->
            incrementWordIndex()
            wordAtIndex(state.wordIndex)?.let {
                log.d("next for $i ${it.sub}")
                state.movieToWordMap[i] = it
                movies[i].setSubtitle(it.sub)
            }
        }
    }

    private fun playNext() {
        //view.active = -1
        log.d("finished(${state.activeIndex}) - $subtitleToDisplay")
        movies[state.activeIndex].volume(0f)
        movies[state.activeIndex].pause()
        val lastIndex = state.activeIndex
        state.activeIndex = state.activeIndex.wrapInc()

        state.movieToWordMap[state.activeIndex]?.let {
            //view.active = state.activeIndex
            movies[state.activeIndex].volume(state.volume)
            movies[state.activeIndex].play()
            log.d("playing(${state.activeIndex}) - ${it.sub.text}")
        } ?: run {
            log.d("Nothing to play")
            speechUI.playing = false
            view.active = -1
            //view.recordStop()
        }
        loadNextWord(lastIndex)
    }

    private fun loadNextWord(playerIndex: Int) {
        incrementWordIndex()
        wordAtIndex(state.wordIndex)?.let {
            log.d("next for $playerIndex ${it.sub}")
            movies[playerIndex].setSubtitle(it.sub)
            state.movieToWordMap[playerIndex] = it
            log.d(it.sub.text[0])
        } ?: run {
            state.movieToWordMap[playerIndex] = null
            log.d("No more words to load playerIndex=${playerIndex} wordIndex=${state.wordIndex}")
        }
    }

    private fun incrementWordIndex() {
        state.wordIndex++
        if (state.looping) {
            state.wordIndex = state.wordIndex % (state.words?.words?.size ?: 0)
        }
    }
    // endregion

    companion object {

        val BASE = "${System.getProperty("user.dir")}/speecher"
        private val BASE_RESOURCES = "$BASE/src/main/resources"

        //var DEF_BASE_PATH = "$BASE/ytcaptiondl/Never Is Now 2019 _ ADL International Leadership Award Presented to Sacha Baron Cohen-ymaWq5yZIYM"
        //var DEF_BASE_PATH = "$BASE/ytcaptiondl/In full - Boris Johnson holds press conference as he defends virus strategy-8aY5J296p9Y"
        val DEF_BASE_PATH = "$BASE/ytcaptiondl/Boris Johnson - 3rd Margaret Thatcher Lecture (FULL)-Dzlgrnr1ZB0"
        val DEF_MOVIE_PATH = "$DEF_BASE_PATH.mp4"

        val DEF_WRITE_SRT_PATH = "$DEF_BASE_PATH.write.srt"

        val RECORD_PATH = "$BASE/record"

        @JvmStatic
        val module = module {

            single {
                GeneratorView(
                    state = get(),
                    pExecutor = get(),
                    log = get()
                )
            }
            factory<GeneratorContract.View> { get<GeneratorView>() }
            single { GeneratorState() }
            factory<PApplet> { get<GeneratorView>() }
            factory<MovieContract.Sketch> { get<GeneratorView>() }
        }

    }

}

