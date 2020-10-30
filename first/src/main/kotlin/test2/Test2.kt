package test2

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PFont
import processing.core.PImage
import processing.video.Movie

fun main(args: Array<String>) {
    Test2().run()
}

class Test2 : PApplet() {

    companion object {
        private val BASE_RESOURCES = "${System.getProperty("user.dir")}/first/src/main/resources"
        var MOVIE_PATH = "${System.getProperty("user.home")}/Dropbox/Photos/20170615_185709.mp4"
        private val SPRITE_PATH = "${BASE_RESOURCES}/test2/sprite.jpg"
        val LIB_PATH = "${System.getProperty("user.home")}/Documents/Processing/libraries/video/library/macosx64"
    }

    private lateinit var f: PFont
    private lateinit var myMovie: Movie
    private lateinit var ps: ParticleSystem
    private lateinit var sprite: PImage

    init {
         // https://forum.processing.org/two/discussion/7593/processing-2-2-1-in-maven
         System.setProperty("jna.library.path", "$LIB_PATH/")
         System.setProperty("gstreamer.library.path", "$LIB_PATH/")
         System.setProperty("gstreamer.plugin.path", "$LIB_PATH/plugins/")
    }

    override fun settings() {
        size(640, 360, PConstants.P2D)
    }

    override fun setup() {
        background(0)
        sprite = loadImage(SPRITE_PATH)
        ps = ParticleSystem(this, 1000, sprite)
        // Writing to the depth buffer is disabled to avoid rendering
        // artifacts due to the fact that the particles are semi-transparent
        // but not z-sorted.
        hint(PConstants.DISABLE_DEPTH_MASK)
        // Create the font
        printArray(PFont.list())
//        f = createFont("ArialMT", 24f);
        f = createFont("Thonburi", 24f)
        textFont(f)
        textSize(24f)
        textAlign(PConstants.CENTER, PConstants.CENTER)
        // TODO figure out how to configure movie libraries
        myMovie = Movie(this, MOVIE_PATH)
        myMovie.loop()
    }

    override fun draw() {
        background(0)
        fill(255f, 204f, 0f)
        myMovie.volume(0.0f)
        image(myMovie, 0f, 0f)
        ps.update()
        ps.display()
        ps.setEmitter(mouseX.toFloat(), mouseY.toFloat())
        text("Love without hope", 320f, 180f)
    }

    // Called every time a new frame is available to read
    // setup via reflection .. tsk tsk
    // needs class to be public to access method
    fun movieEvent(m: Movie) {
        m.read()
    }

    fun run() {
        runSketch(arrayOf(this::class.java.simpleName), this)
    }

}