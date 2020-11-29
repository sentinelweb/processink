package speecher.generator

import io.reactivex.Scheduler
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.get
import org.koin.core.qualifier.named
import org.koin.dsl.module
import processing.core.PApplet
import speecher.di.Modules
import speecher.domain.Sentence
import speecher.domain.Subtitles
import speecher.generator.bank.MovieBankContract
import speecher.generator.bank.MovieBankCreator
import speecher.generator.movie.MovieContract
import speecher.generator.movie.MovieCreator
import speecher.generator.osc.OscContract
import speecher.generator.ui.SpeechContract
import speecher.scheduler.SchedulerModule.PROCESSING
import speecher.scheduler.SchedulerModule.SWING
import speecher.util.wrapper.LogWrapper
import java.awt.Color
import java.awt.Font
import java.awt.geom.Rectangle2D
import java.io.File

fun main() {
    startKoin {
        modules(Modules.allModules)
    }
    GeneratorPresenter()
}

class GeneratorPresenter : GeneratorContract.Presenter,
    SpeechContract.Listener,
    KoinComponent,
    MovieBankContract.Listener {

    private val view: GeneratorContract.View = get()
    private val state: GeneratorState = get()
    private val pScheduler: Scheduler = get(named(PROCESSING))
    private val swingScheduler: Scheduler = get(named(SWING))
    private val speechUI: SpeechContract.External = get()
    private val bankCreator: MovieBankCreator = get()
    private val movieCreator: MovieCreator = get()
    private val log: LogWrapper = get()
    private val oscController: OscContract.Controler = get()

    private var bank: MovieBankContract.External? = null
    private var preview: MovieContract.External? = null

    override val subtitleToDisplay: String
        get() = bank?.subtitleToDisplay ?: "-"
    override val selectedFontColor: Color?
        get() = speechUI.selectedFontColor
    override val selectedFont: Font?
        get() = speechUI.selectedFont

    init {
        log.tag(this)
        view.presenter = this
        view.run()
        speechUI.listener = this
        speechUI.showWindow()
    }

    // region Presenter
    override fun initialise() {
        speechUI.initialise()
        bankCreator.create(this).also { (e, v) ->
            bank = e
            view.setBankview(v)
        }
    }
    // endregion

    override fun onPlayFinished() {
        speechUI.playing = false
    }

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
        bank?.cleanup()
        bank?.loadMovieFile(movie)
        preview?.cleanup()
        log.d("preview creating")
        preview = movieCreator.create(0, log, previewListener).also {
            log.d("preview created")
            it.config = it.config.copy(bounds = Rectangle2D.Float(0f, 0f, 320f, 240f))
            it.openMovie(movie)
            it.volume(0f)
            it.pause()
            view.setPreview(it.view)
        }
    }

    private val previewListener = object : MovieContract.Listener {
        override fun onReady() {

        }

        override fun onSubtitleStart(sub: Subtitles.Subtitle) {

        }

        override fun onSubtitleFinished(sub: Subtitles.Subtitle) {
            state.previewWord?.let {
                preview?.setSubtitle(it.sub)
            }
        }

        override fun onPlaying() {
            preview?.volume(0.2f)
        }

    }

    override fun preview(word: Sentence.Word?) {
        state.previewWord = word
        word?.let {
            preview?.apply {
                if (playState != MovieContract.State.PLAYING) {
                    setSubtitle(word.sub)
                    play()
                    volume(0.2f)
                }
                this@GeneratorPresenter.view.showPrewiew = true
            }
        } ?: run {
            preview?.pause()
            this@GeneratorPresenter.view.showPrewiew = false
        }
    }

    override fun onOscReceiveToggled() {
        if (speechUI.oscReceiver) {
            oscController.shutdown()
            speechUI.oscReceiver = false
        } else {
            oscController.initialise()
            speechUI.oscReceiver = true

        }
    }

    override fun updateFontColor() {
        view.updateFontColor()
    }

    override fun updateFont() {
        updateViewFont()
    }

    private fun updateViewFont() {
        view.setFont(selectedFont?.fontName ?: "Thonburi", selectedFont?.size?.toFloat() ?: 24f)
    }

    override fun onShutdown() {
        try {
            bank?.cleanup()
            preview?.cleanup()
            oscController.shutdown()
            view.cleanup()
        } catch (t: Throwable) {
            log.e("onShutdown", t)
        }
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

