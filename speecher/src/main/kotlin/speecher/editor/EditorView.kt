package speecher.editor

import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ext.getOrCreateScope
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PFont
import processing.video.Movie
import speecher.di.Modules
import speecher.editor.transport.TransportContract
import speecher.scheduler.ProcessingExecutor
import speecher.scheduler.SchedulerModule.PROCESSING
import speecher.scheduler.SchedulerModule.SWING
import java.awt.Dimension
import java.awt.geom.Rectangle2D
import java.io.File
import javax.swing.SwingUtilities

fun main() {
    startKoin {
        modules(Modules.allModules)
    }
    EditorView().run()
}

class EditorView() : PApplet(), EditorContract.View, KoinComponent {

    private val scope = this.getOrCreateScope()
    private val presenter: EditorContract.Presenter = scope.get()
    private val pExecutor: ProcessingExecutor = scope.get()

    private lateinit var f: PFont
    private lateinit var movie: Movie
    private var movieDimension: Dimension? = null
    private var screenRect: Rectangle2D.Float? = null

    init {
        // https://forum.processing.org/two/discussion/7593/processing-2-2-1-in-maven
        System.setProperty("jna.library.path", "${LIB_PATH}/")
        System.setProperty("gstreamer.library.path", "${LIB_PATH}/")
        System.setProperty("gstreamer.plugin.path", "${LIB_PATH}/plugins/")
    }

    // region Processing
    fun run() {
        runSketch(arrayOf(this::class.java.simpleName), this)
    }

    override fun settings() {
        size(1024, 768, PConstants.P2D)
    }

    override fun setup() {
        background(0)
        // Create the font
        printArray(PFont.list())
        f = createFont("Thonburi", 24f)
        textFont(f)
        textSize(20f)
        textAlign(PConstants.CENTER, PConstants.CENTER)
        SwingUtilities.invokeLater { presenter.initialise() }
    }

    override fun draw() {
        while (pExecutor.workQueue.size > 0) {
            pExecutor.workQueue.take().run()
        }
        background(0)
        fill(255f, 255f, 255f)
        if (this::movie.isInitialized) {
            screenRect?.apply {
                image(movie, x, y, width, height)
            }
        }
        fill(255f, 255f, 0f)
        presenter.currentReadSubtitle?.let { text(it, width / 2f, height - 50f) }
        fill(255f, 128f, 0f)
        presenter.currentWriteSubtitle?.let { text(it, width / 2f, height - 100f) }
    }
    // endregion

    // region movie
    fun movieEvent(m: Movie) {
        m.read()
        // println("FR: ${myMovie.frameRate} W:${myMovie.width} H:${myMovie.height}")
        if (movieDimension == null) {
            movieDimension = Dimension(movie.width, movie.height)
            val movieAspect = movieDimension!!.width / movieDimension!!.height.toFloat()
            val screenAspect = width / height.toFloat()
            screenRect = Rectangle2D.Float(
                0f,
                ((height - height * screenAspect / movieAspect) / 2f),
                width.toFloat(),
                (width / movieAspect)
            )
            presenter.duration(movie.duration())
            presenter.movieInitialised()
        }
        presenter.position(movie.time())
    }
    // endregion

    // region View
    override fun openMovie(file: File) {
        if (this::movie.isInitialized) {
            movie.stop()
            movie.dispose()
        }
        movieDimension = null
        screenRect = null
        movie = Movie(this, file.absolutePath)
        movie.play()
        presenter.setPlayState(TransportContract.UiDataType.MODE_PLAYING)
    }

    override fun setMovieSpeed(speed: Float) {
        if (this::movie.isInitialized) pExecutor.execute { movie.speed(speed) }
    }

    override fun play() {
        if (this::movie.isInitialized) {
            pExecutor.execute { movie.play() }
            presenter.setPlayState(TransportContract.UiDataType.MODE_PLAYING)
        }
    }

    override fun pause() {
        if (this::movie.isInitialized) {
            pExecutor.execute { movie.pause() }
            presenter.setPlayState(TransportContract.UiDataType.MODE_PAUSED)
        }
    }

    override fun volume(vol: Float) {
        if (this::movie.isInitialized) pExecutor.execute { movie.volume(vol) }
    }

    override fun seekTo(positionSec: Float) {
        if (this::movie.isInitialized)
            pExecutor.execute { movie.jump(positionSec) }

    }

    // endregion

    companion object {
        val BASE = "${System.getProperty("user.dir")}/speecher"
        private val BASE_RESOURCES = "$BASE/src/main/resources"
        var DEF_BASE_PATH =
            "$BASE/ytcaptiondl/Never Is Now 2019 _ ADL International Leadership Award Presented to Sacha Baron Cohen-ymaWq5yZIYM"

        //var DEF_BASE_PATH = "$BASE/ytcaptiondl/In full - Boris Johnson holds press conference as he defends virus strategy-8aY5J296p9Y"
        //var DEF_BASE_PATH = "$BASE/ytcaptiondl/Boris Johnson - 3rd Margaret Thatcher Lecture (FULL)-Dzlgrnr1ZB0"
        var DEF_MOVIE_PATH = "$DEF_BASE_PATH.mp4"
        var DEF_SRT_PATH = "$DEF_BASE_PATH.en.srt"

        val LIB_PATH = "${System.getProperty("user.home")}/Documents/Processing/libraries/video/library/macosx64"

        @JvmStatic
        val viewModule = module {
            scope(named<EditorView>()) {
                scoped<EditorContract.View> { getSource() }
                scoped<EditorContract.Presenter> {
                    EditorPresenter(
                        get(), get(), get(), get(), get(), get(),
                        get(named(PROCESSING)), get(named(SWING))
                    )
                }
                scoped { EditorState() }
            }
        }
    }


}