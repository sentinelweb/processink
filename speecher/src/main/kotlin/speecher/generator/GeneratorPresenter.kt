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
        get() = if (state.playIndex > -1) state.words[state.playIndex].text.toString() else "-"

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
                state.playIndex = -1
                state.startTime = System.currentTimeMillis()
                view.setActive(0)
                playNext()
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

    private fun playNext() {
        state.playIndex++
        state.playIndex = state.playIndex % state.words.size
        view.seekTo(0, state.words[state.playIndex].fromSec)
    }

    override fun onMovieEvent(indexOf: Int, pos: Float) {
        if (state.playIndex > -1) {
            if (pos > state.words[state.playIndex].toSec) {
                playNext()
            }
        }
    }

    // region Movie
    private fun setMovieFile(file: File) {
        openMovieSingle(file)
            .observeOn(swingScheduler)
            .subscribe({

            }, { it.printStackTrace() })
            .also { disposables.add(it) }
    }

    private fun openMovieSingle(file: File): Single<File> {
        return Single.just(file)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { state.movieFile = it }
            .subscribeOn(pScheduler)
            .doOnSuccess { view.openMovie(0, it) }

    }

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

