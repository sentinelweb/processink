package speecher.generator

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PFont
import processing.video.Movie
import speecher.generator.movie.MovieContract
import speecher.scheduler.ProcessingExecutor
import java.io.File

class GeneratorView constructor(
//    private val presenter: GeneratorContract.Presenter,
    private val state: GeneratorState,
    private val pExecutor: ProcessingExecutor
) : PApplet(), GeneratorContract.View, MovieContract.Sketch {

    override lateinit var presenter: GeneratorContract.Presenter
    override var active: Int = -1

    private lateinit var f: PFont
    private val movieViews: MutableList<MovieContract.View> = mutableListOf()

    init {
        // https://forum.processing.org/two/discussion/7593/processing-2-2-1-in-maven
        System.setProperty("jna.library.path", "${LIB_PATH}/")
        System.setProperty("gstreamer.library.path", "${LIB_PATH}/")
        System.setProperty("gstreamer.plugin.path", "${LIB_PATH}/plugins/")
    }

    // region Processing
    override fun settings() {
        size(1024, 768, PConstants.P2D)
    }

    override fun setup() {
        background(0)
        f = createFont("Thonburi", 24f)
        textFont(f)
        textSize(20f)
        textAlign(PConstants.CENTER, PConstants.CENTER)
        presenter.initialise()
    }

    override fun draw() {
        while (pExecutor.workQueue.size > 0) {
            pExecutor.workQueue.take().run()
        }
        background(0)
        fill(255f, 255f, 255f)
        //movieViews.forEach { it.render() }
        if (active > -1) movieViews[active].render()
        fill(255f, 255f, 0f)
        presenter.subtitle?.let { text(it, width / 2f, height - 25f) }
    }
    // endregion

    // region movie
    fun movieEvent(m: Movie) {
        m.read()
        movieViews.find { it.hasMovie(m) }?.movieEvent(m)
    }
    // endregion

    // region View
    override fun run() {
        runSketch(arrayOf(this::class.java.simpleName), this)
    }

    override fun openMovie(i: Int, file: File) {
        // todo
    }
    // endregion

    // region MovieContract.Sketch
    override fun addView(v: MovieContract.View) {
        movieViews.add(v)
    }

    override fun cleanup() {
        movieViews.forEach { it.cleanup() }
    }

    companion object {
        val LIB_PATH = "${System.getProperty("user.home")}/Documents/Processing/libraries/video/library/macosx64"

    }


}