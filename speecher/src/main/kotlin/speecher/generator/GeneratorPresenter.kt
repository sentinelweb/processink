package speecher.generator

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.get
import org.koin.core.qualifier.named
import org.koin.dsl.module
import processing.core.PApplet
import speecher.di.Modules
import speecher.domain.Subtitles
import speecher.generator.movie.MovieContract
import speecher.generator.movie.MoviePresenter
import speecher.interactor.srt.SrtInteractor
import speecher.scheduler.SchedulerModule.PROCESSING
import speecher.scheduler.SchedulerModule.SWING
import java.io.File

fun main() {
    startKoin {
        modules(Modules.allModules)
    }
    GeneratorPresenter()
}

class GeneratorPresenter : GeneratorContract.Presenter, KoinComponent {
    private val view: GeneratorContract.View = get()
    private val state: GeneratorState = get()
    private val pScheduler: Scheduler = get(named(PROCESSING))
    private val swingScheduler: Scheduler = get(named(SWING))
    private val srtInteractor: SrtInteractor = get()

    override val subtitle: String?
        get() = if (state.playingWord > -1) state.words[state.playingWord].text.toString() else "-"
    private val loadIndex: Int
        get() = if (state.activeIndex == 0) 1 else 0
    private val movies = mutableListOf<MovieContract.External>()

    private val disposables: CompositeDisposable = CompositeDisposable()

    init {
        view.presenter = this
        view.run()
    }

    override fun initialise() {
        Single.concat(
            openMovieSingle(File(DEF_MOVIE_PATH)),
            srtOpenSingle(File(DEF_WRITE_SRT_PATH))
        )
            .subscribeOn(Schedulers.computation())
            .observeOn(pScheduler)
            .doOnComplete {
                buildWordList()
                state.wordIndex = -1
                state.startTime = System.currentTimeMillis()
                movies.forEach {
                    it.volume(0.05f)
                    it.listener = object : MovieContract.Listener {
                        override fun onSubtitleStart(sub: Subtitles.Subtitle) {

                        }

                        override fun onSubtitleFinished(sub: Subtitles.Subtitle) {
                            playNext()
                        }

                    }
                }
                playFirst()
            }
            .subscribe({
                when (it) {
                    is File -> println("Opened movie file : $it")
                    is Subtitles -> println("Opened subtitles : ${it.timedTexts.size} subtitles")
                }
            }, { it.printStackTrace() })
            .also { disposables.add(it) }
    }

    private fun playNext() {
        state.activeIndex = loadIndex
        state.wordIndex++
        state.wordIndex = state.wordIndex % state.words.size
        movies[loadIndex].setSubtitle(state.words[state.wordIndex])
        movies[state.activeIndex].play()
    }

    private fun playFirst() {
        state.activeIndex = 0
        movies[state.activeIndex].play()
        movies[loadIndex].pause()
        state.wordIndex++
        //state.wordIndex = state.wordIndex % state.words.size
        movies[state.activeIndex].setSubtitle(state.words[state.wordIndex])
        state.wordIndex++
        //state.wordIndex = state.wordIndex % state.words.size
        movies[loadIndex].setSubtitle(state.words[state.wordIndex])
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
            .subscribeOn(Schedulers.io())
            .doOnSuccess { state.movieFile = it }
            .subscribeOn(pScheduler)
            .doOnSuccess { file ->
                movies.add(MoviePresenter())
                movies.add(MoviePresenter())
                movies.forEach { movie -> movie.openMovie(file) }
            }
    }
    // endregion

    private fun buildWordList() {
        state.words = state.speakString
            .split(" ")
            .map { word -> state.subs?.timedTexts?.find { it.text[0] == word } }
            .filterNotNull()
    }

    private fun srtOpenSingle(file: File) =
        srtInteractor.read(file)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                state.subs = it
                state.srtFile = file
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

