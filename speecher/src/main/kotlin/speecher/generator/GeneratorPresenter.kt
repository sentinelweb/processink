package speecher.generator

import io.reactivex.Scheduler
import org.koin.core.KoinComponent
import org.koin.core.context.KoinContextHandler.get
import org.koin.core.context.startKoin
import org.koin.core.get
import org.koin.core.qualifier.named
import org.koin.dsl.module
import processing.core.PApplet
import speecher.di.Modules
import speecher.domain.Sentence
import speecher.generator.bank.MovieBankContract
import speecher.generator.bank.MovieBankPresenter
import speecher.generator.bank.MovieBankView
import speecher.generator.movie.MovieContract
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


    private var bank: MovieBankContract.External? = null

    override val subtitleToDisplay: String
        get() = bank?.subtitleToDisplay ?: "-"
    override val selectedFontColor: Color?
        get() = speechUI.selectedFontColor
    override val selectedFont: Font?
        get() = speechUI.selectedFont
//    override val playEventLatency: Float?
//        get() = speechUI.playEventLatency

    init {
        log.tag(this)
        view.presenter = this
        view.run()
        speechUI.listener = this
        speechUI.showWindow()

    }

    private fun makeBank() {
        view.bankView = MovieBankView().also { view ->
            bank = MovieBankPresenter(MovieBankContract.State(), view, 10).also { bank ->
                bank.listener = object : MovieBankContract.Listener {
                    override fun onPlayFinished() {
                        speechUI.playing = false
                        // view.recordStop()
                    }
                }
            }
        }
    }

    // region Presenter
    override fun initialise() {
        speechUI.initialise()
        makeBank()
    }

    // endregion

    // region SpeechContract.Listener
    override fun sentenceChanged(sentence: Sentence) {
        state.words = sentence
        bank?.apply { config = config.copy(words = sentence) }
    }

    override fun play() {
        speechUI.playing = true
        // startRecording()
        bank?.startPlaying()
    }

    private fun startRecording() {
        File(RECORD_PATH).mkdir()
        view.recordNew(RECORD_PATH)
    }

    override fun pause() {
        bank?.pause()
        speechUI.playing = false
    }

    override fun updateFontColor() {
        view.updateFontColor()
    }

    override fun updateFont() {
        updateViewFont()
    }

    override fun updateBank() {
        bank?.apply {
            config = config.copy(
                volume = speechUI.volume,
                looping = speechUI.looping,
                playEventLatency = speechUI.playEventLatency ?: 0.05f,
                words = state.words
            )
        }
    }

    override fun loadMovieFile(movie: File) {
        view.cleanup()
        bank?.loadMovieFile(movie)
    }

    private fun updateViewFont() {
        view.setFont(selectedFont?.fontName ?: "Thonburi", selectedFont?.size?.toFloat() ?: 24f)
    }

    // endregion

    companion object {

        internal val BASE = "${System.getProperty("user.dir")}/speecher"
        internal val RECORD_PATH = "$BASE/record"

        @JvmStatic
        val module = module {

            single {
                GeneratorView(
                    pExecutor = get(),
                    log = get()
                )
            }
            factory<GeneratorContract.View> { get<GeneratorView>() }
            single { GeneratorState() }
            factory<PApplet> { get<GeneratorView>() }
        }

    }

}

