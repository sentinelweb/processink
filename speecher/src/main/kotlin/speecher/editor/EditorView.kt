package speecher.editor

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PFont
import processing.video.Movie
import speecher.editor.transport.TransportContract
import speecher.editor.transport.TransportPresenter
import speecher.editor.transport.TransportState
import speecher.editor.transport.TransportView
import speecher.interactor.srt.SrtFileReader
import speecher.interactor.srt.SrtFileWriter
import speecher.interactor.srt.SrtInteractor
import speecher.interactor.srt.SrtMapper
import speecher.util.format.TimeFormatter
import java.awt.Dimension
import java.awt.geom.Rectangle2D
import java.io.File

fun main() {
    val view = EditorView()
    val transport = TransportPresenter(TransportView(), TransportState(), TimeFormatter())
    view.presenter = EditorPresenter(
        view,
        EditorState(),
        transport,
        SrtInteractor(
            SrtFileReader(SrtMapper()), SrtFileWriter(SrtMapper())
        )
    )
    view.run()
}

class EditorView() : PApplet(), EditorContract.View {

    lateinit var presenter: EditorPresenter
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
        textSize(24f)
        textAlign(PConstants.CENTER, PConstants.CENTER)

        println("MOVIE_PATH : $MOVIE_PATH")

        movie = Movie(this, MOVIE_PATH)
        movie.play()
        presenter.setPlayState(TransportContract.UiDataType.MODE_PLAYING)
        println("FR: ${movie.frameRate} W:${movie.width} H:${movie.height}")
    }

    override fun draw() {
        background(0)
        fill(255f, 255f, 255f)
        screenRect?.apply {
            image(movie, x, y, width, height)
        }
        text("Subtitle", width / 2f, height - 50f)
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
        if (this::movie.isInitialized) movie.speed(speed)
    }

    override fun play() {
        if (this::movie.isInitialized) {
            movie.play()
            presenter.setPlayState(TransportContract.UiDataType.MODE_PLAYING)
        }
    }

    override fun pause() {
        if (this::movie.isInitialized) {
            movie.pause()
            presenter.setPlayState(TransportContract.UiDataType.MODE_PAUSED)
        }
    }

    override fun volume(vol: Float) {
        if (this::movie.isInitialized) movie.volume(vol)
    }

    override fun seekTo(positionSec: Float) {
        if (this::movie.isInitialized) movie.jump(positionSec)
    }

    // endregion

    companion object {
        private val BASE_RESOURCES = "${System.getProperty("user.dir")}/speecher/src/main/resources"
        var MOVIE_PATH =
            "${System.getProperty("user.dir")}/speecher/ytcaptiondl/Boris Johnson - 3rd Margaret Thatcher Lecture (FULL)-Dzlgrnr1ZB0.mp4"

        val LIB_PATH = "${System.getProperty("user.home")}/Documents/Processing/libraries/video/library/macosx64"

        //var MOVIE_PATH = "${System.getProperty("user.home")}/Dropbox/Photos/20170615_185709.mp4"
    }


}