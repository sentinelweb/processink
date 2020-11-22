package speecher.generator

import io.reactivex.Scheduler
import io.reactivex.Single
import org.koin.core.KoinComponent
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
import java.io.File

fun main() {
    startKoin {
        modules(Modules.allModules)
    }
    GeneratorPresenter()
}

class GeneratorPresenter : GeneratorContract.Presenter, KoinComponent, SpeechContract.Listener {
    private val view: GeneratorContract.View = get()
    private val state: GeneratorState = get()
    private val pScheduler: Scheduler = get(named(PROCESSING))
    private val swingScheduler: Scheduler = get(named(SWING))
    private val speechUI: SpeechContract.External = get()


    override val subtitle: String?
        get() = if (state.playingWord > -1) subtitle(state.playingWord)?.text.toString() else "-"

    private val movies = mutableListOf<MovieContract.External>()
    private fun subtitle(index: Int) = state.words?.words?.get(index)?.sub

    private fun wrapInc(i: Int) = if (i + 1 < movies.size) i + 1 else 0

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

    inner class MvListener() : MovieContract.Listener {
        override fun onReady() {

        }

        override fun onSubtitleStart(sub: Subtitles.Subtitle) {

        }

        override fun onSubtitleFinished(sub: Subtitles.Subtitle) {
            playNext()
        }
    }


    private fun playNext() {
        movies[state.activeIndex].pause()
        val lastIndex = state.activeIndex
        state.activeIndex = wrapInc(state.activeIndex)

        state.wordIndex++
        state.wordIndex = state.wordIndex % (state.words?.words?.size ?: 0)
        state.words?.words?.get(state.wordIndex)?.sub?.let {
            movies[lastIndex].setSubtitle(it)
            println(it.text[0])
        }
        //movies[state.activeIndex].volume(1f)
        movies[state.activeIndex].play()
        println("playing(${state.activeIndex})")
    }
//
//    private fun playFirst() {
//        state.activeIndex = 0
//        movies[state.activeIndex].play()
//        //movies[loadIndex].pause()
//        state.wordIndex++
//        //state.wordIndex = state.wordIndex % state.words.size
//        movies[state.activeIndex].setSubtitle(state.words[state.wordIndex])
//        state.wordIndex++
//        //state.wordIndex = state.wordIndex % state.words.size
//        //movies[loadIndex].setSubtitle(state.words[state.wordIndex])
//    }

    private fun startPlaying() {
        state.wordIndex = 0
        state.activeIndex = 0
        subtitle(state.wordIndex)?.apply {
            movies[state.activeIndex].setSubtitle(this)
        }

        //movies[state.activeIndex].volume(1f)
        movies[state.activeIndex].play()
        (1..movies.size - 1).forEach { i ->
            state.wordIndex++
            state.wordIndex = state.wordIndex % (state.words?.words?.size ?: 0)
            subtitle(state.wordIndex)?.apply {
                movies[i].setSubtitle(this)
            }
        }
    }

    override fun onMovieEvent(index: Int, pos: Float) {
//        if (state.playingWord > -1) {
//            //println("presenter.onMovieEvent: index=$index, pos=$pos")
//            if (index == state.activeIndex && pos > state.words[(state.playingWord)].toSec) {
//                //println("presenter.onMovieEvent loopend: ${state.activeIndex} ${state.wordIndex}")
//                view.pause(state.activeIndex)
//                state.activeIndex = loadIndex
//                view.setActive(state.activeIndex)
//                view.play(state.activeIndex)
//                state.playingWord = state.wordIndex
//                state.wordIndex++
//                state.wordIndex = state.wordIndex % state.words.size
//                view.seekTo(loadIndex, state.words[state.wordIndex].fromSec)
//
//
//            }
//        }
    }

    // region Movie
    private fun openMovieSingle(file: File): Single<File> {
        return Single.just(file)
            .doOnSuccess { state.movieFile = it }
            .observeOn(pScheduler)
            .doOnSuccess {
                (0..20).forEach {
                    makeMovie(it, file)
                }
            }
    }

    private fun makeMovie(i: Int, file: File) = MoviePresenter(i).apply {
        listener = MvListener()
        movies.add(this)
        openMovie(file)
        //volume(0f)
    }
    // endregion

    override fun sentenceChanged(sentence: Sentence) {
        state.words = sentence
    }

    override fun play() {
        startPlaying()
    }

    override fun pause() {
        movies.forEach { it.pause() }
    }

    companion object {

        val BASE = "${System.getProperty("user.dir")}/speecher"
        private val BASE_RESOURCES = "$BASE/src/main/resources"

        //var DEF_BASE_PATH = "$BASE/ytcaptiondl/Never Is Now 2019 _ ADL International Leadership Award Presented to Sacha Baron Cohen-ymaWq5yZIYM"
        //var DEF_BASE_PATH = "$BASE/ytcaptiondl/In full - Boris Johnson holds press conference as he defends virus strategy-8aY5J296p9Y"
        var DEF_BASE_PATH = "$BASE/ytcaptiondl/Boris Johnson - 3rd Margaret Thatcher Lecture (FULL)-Dzlgrnr1ZB0"
        var DEF_MOVIE_PATH = "$DEF_BASE_PATH.mp4"

        var DEF_WRITE_SRT_PATH = "$DEF_BASE_PATH.write.srt"

        @JvmStatic
        val module = module {

            single {
                GeneratorView(
                    state = get(),
                    pExecutor = get()
                )
            }
            factory<GeneratorContract.View> { get<GeneratorView>() }
            single { GeneratorState() }
            factory<PApplet> { get<GeneratorView>() }
            factory<MovieContract.Sketch> { get<GeneratorView>() }
        }

    }


}

