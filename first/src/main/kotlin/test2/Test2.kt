package test2

import com.sun.jna.NativeLibrary
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PFont
import processing.core.PImage
import processing.video.Movie

fun main(args: Array<String>) {
    val sketch = Test2()
    sketch.run()
}

class Test2 : PApplet() {

    companion object {
        const val MOVIE_PATH  = "/Users/robmunro/Dropbox/Photos/20170615_185709.mp4"
        const val SPRITE_PATH  = "/Users/robmunro/Documents/Processing/sketch_191031a/data/sprite.png"
        const val LIB_PATH  = "file:/Users/robmunro/Documents/Processing/libraries/video/library"

    }

    private lateinit var f: PFont
    private lateinit var myMovie: Movie
    private lateinit var ps: ParticleSystem
    private lateinit var sprite: PImage

    init {
        //NativeLibrary.addSearchPath("gstreamer-0.10",LIB_PATH)
//        val path = System.getProperty("java.library.path")
//        System.setProperty("java.library.path", "$path:$LIB_PATH")
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
        //f = createFont("SourceCodePro-Regular.ttf", 24);
        //textFont(f);
        textSize(24f)
        textAlign(PConstants.CENTER, PConstants.CENTER)
        // TODO figure out how to configure movie libraries
//        myMovie = Movie(this, MOVIE_PATH)
//        myMovie.loop()
    }

    override fun draw() {
        background(0)
        fill(255f, 204f, 0f)
//        image(myMovie, 0f, 0f)
        ps.update()
        ps.display()
        ps.setEmitter(mouseX.toFloat(), mouseY.toFloat())
        text("Love without hope", 320f, 180f)
    }

    // Called every time a new frame is available to read
    // setup via reflection .. tsk tsk
    fun movieEvent(m: Movie) {
        m.read()
    }

    fun run() {
        runSketch(arrayOf(this::class.java.simpleName), this)
    }
}