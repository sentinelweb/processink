package speecher.editor

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PFont
import processing.video.Movie
import proto.VidTest
import speecher.editor.transport.TransportPresenter
import speecher.editor.transport.TransportState
import speecher.editor.transport.TransportView
import java.awt.Dimension

fun main() {
    val view = EditorView()
    val transport = TransportPresenter(TransportView(), TransportState())
    view.presenter = EditorPresenter(view, EditorState(), transport)
    view.run()
}

class EditorView() : PApplet(), EditorContract.View {

    lateinit var presenter: EditorPresenter
    private lateinit var f: PFont
    private lateinit var myMovie: Movie
    private lateinit var dimension: Dimension

    init {
        // https://forum.processing.org/two/discussion/7593/processing-2-2-1-in-maven
        System.setProperty("jna.library.path", "${VidTest.LIB_PATH}/")
        System.setProperty("gstreamer.library.path", "${VidTest.LIB_PATH}/")
        System.setProperty("gstreamer.plugin.path", "${VidTest.LIB_PATH}/plugins/")
    }

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

        myMovie = Movie(this, MOVIE_PATH)
        myMovie.loop()
        println("FR: ${myMovie.frameRate} W:${myMovie.width} H:${myMovie.height}")
    }

    override fun draw() {
        background(0)
        fill(255f, 255f, 255f)
        myMovie.volume(0.7f)
        if (this::dimension.isInitialized) {
            val movieAspect = dimension.width / dimension.height.toFloat()
            val screenAspect = width / height.toFloat()
            image(
                myMovie,
                0f,
                (height - height * screenAspect / movieAspect) / 2f,
                width.toFloat(),
                width / movieAspect
            )
        }
        text("Subtitle", width / 2f, height - 50f)
    }

    fun movieEvent(m: Movie) {
        m.read()
        //println("FR: ${myMovie.frameRate} W:${myMovie.width} H:${myMovie.height}")
        dimension = Dimension(myMovie.width, myMovie.height)
    }

    companion object {
        private val BASE_RESOURCES = "${System.getProperty("user.dir")}/speecher/src/main/resources"
        var MOVIE_PATH =
            "${System.getProperty("user.dir")}/speecher/ytcaptiondl/Boris Johnson - 3rd Margaret Thatcher Lecture (FULL)-Dzlgrnr1ZB0.mp4"
        val LIB_PATH = "${System.getProperty("user.home")}/Documents/Processing/libraries/video/library/macosx64"

        //var MOVIE_PATH = "${System.getProperty("user.home")}/Dropbox/Photos/20170615_185709.mp4"

    }

}