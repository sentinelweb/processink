package speecher.generator

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ext.getOrCreateScope
import speecher.di.Modules
import speecher.domain.Subtitles
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

class GeneratorPresenter : GeneratorContract.Presenter {
    private val scope = this.getOrCreateScope()
    private val view: GeneratorContract.View = scope.get()
    private val state: GeneratorState = scope.get()
    private val pScheduler: Scheduler = scope.get(named(PROCESSING))
    private val swingScheduler: Scheduler = scope.get(named(SWING))
    private val srtInteractor: SrtInteractor = scope.get()

    override val subtitle: String?
        get() = if (state.playingWord > -1) state.words[state.playingWord].text.toString() else "-"
    private val loadIndex: Int
        get() = if (state.activeIndex == 0) 1 else 0
    private val disposables: CompositeDisposable = CompositeDisposable()

    init {
        view.run()
    }

    override fun initialise() {
        Single.concat(
            openMovieSingle(File(DEF_MOVIE_PATH)),
            srtOpenSingle(File(DEF_WRITE_SRT_PATH))
        )
            .subscribeOn(Schedulers.computation())
            .doOnComplete {
                buildWordList()
                state.wordIndex = -1
                state.startTime = System.currentTimeMillis()
//                view.volume(0,0.2f)
//                view.volume(1,0.2f)
                playFirst()
                //testPlayMultiple()
            }
            .observeOn(Schedulers.computation())
            .subscribe({
                when (it) {
                    is File -> println("Opened movie file : $it")
                    is Subtitles -> println("Opened subtitles : ${it.timedTexts.size} subtitles")
                }
            }, { it.printStackTrace() })
            .also { disposables.add(it) }
    }

    private fun testPlayMultiple() {
        view.seekTo(0, 10f)
        view.seekTo(1, 20f)
    }

    //    private fun playFirst() {
//        state.activeIndex = 0
//        state.wordIndex++
//        state.wordIndex = state.wordIndex % state.words.size
//        view.pause(1)
//        view.seekTo(state.activeIndex, state.words[state.wordIndex].fromSec)
//        playNext()
//        loadNext()
//    }
    private fun playFirst() {
        state.activeIndex = 0
        state.wordIndex++
        view.seekTo(0, state.words[state.wordIndex].fromSec)
        view.pause(1)
        state.wordIndex++
        view.seekTo(1, state.words[state.wordIndex].fromSec)
        state.playingWord = 0
    }

    private fun loadNext() {
        println("loadNext: $loadIndex -> ${state.wordIndex}")
        view.pause(loadIndex)
        view.seekTo(loadIndex, state.words[state.wordIndex].fromSec)
    }

    private fun playNext() {
        println("playNext: ${state.activeIndex}")
        view.setActive(state.activeIndex)
        view.play(state.activeIndex)
    }

    override fun onMovieEvent(index: Int, pos: Float) {
        if (state.playingWord > -1) {
            //println("presenter.onMovieEvent: index=$index, pos=$pos")
            if (index == state.activeIndex && pos > state.words[(state.playingWord)].toSec) {
                //println("presenter.onMovieEvent loopend: ${state.activeIndex} ${state.wordIndex}")
                view.pause(state.activeIndex)
                state.activeIndex = loadIndex
                view.setActive(state.activeIndex)
                view.play(state.activeIndex)
                state.playingWord = state.wordIndex
                state.wordIndex++
                state.wordIndex = state.wordIndex % state.words.size
                view.seekTo(loadIndex, state.words[state.wordIndex].fromSec)

//                state.activeIndex = if (state.activeIndex == 0) 1 else 0
//                playNext()
//                state.wordIndex++
//                state.wordIndex = state.wordIndex % state.words.size
//                loadNext()
                //state.activeIndex = if (state.activeIndex == 0) 1 else 0
//                state.wordIndex++
//                state.wordIndex = state.wordIndex % state.words.size
//                view.seekTo(state.activeIndex, state.words[state.wordIndex].fromSec)
            }
        }
    }

    // region Movie
    private fun openMovieSingle(file: File): Single<File> {
        return Single.just(file)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { state.movieFile = it }
            .subscribeOn(pScheduler)
            .doOnSuccess {
                view.openMovie(0, it)
                view.openMovie(1, it)
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

        var DEF_SRT_PATH = "$DEF_BASE_PATH.en.srt"

        var DEF_WRITE_SRT_PATH = "$DEF_BASE_PATH.write.srt"

        @JvmStatic
        val scopeModule = module {
            scope(named<GeneratorPresenter>()) {
                scoped<GeneratorContract.Presenter> { getSource() }
                scoped<GeneratorContract.View> {
                    GeneratorView(
                        presenter = get(),
                        state = get(),
                        pExecutor = get()
                    )
                }
                scoped { GeneratorState() }
            }
        }
    }
}

