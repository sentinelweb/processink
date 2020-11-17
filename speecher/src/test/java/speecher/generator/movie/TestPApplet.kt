package speecher.generator.movie

import org.koin.core.KoinComponent
import org.koin.dsl.module
import processing.core.PApplet
import processing.core.PConstants
import processing.video.Movie
import speecher.scheduler.ProcessingExecutor
import org.koin.core.get as koinGet

class TestPApplet : PApplet(), MovieContract.Sketch, KoinComponent {

    private val pExecutor: ProcessingExecutor = koinGet()

    private val movieViews: MutableList<MovieContract.View> = mutableListOf()

    init {
        // https://forum.processing.org/two/discussion/7593/processing-2-2-1-in-maven
        System.setProperty("jna.library.path", "${LIB_PATH}/")
        System.setProperty("gstreamer.library.path", "${LIB_PATH}/")
        System.setProperty("gstreamer.plugin.path", "${LIB_PATH}/plugins/")
    }

    fun run() {
        runSketch(arrayOf(this::class.java.simpleName), this)
    }

    override fun settings() {
        size(1024, 768, PConstants.P2D)
    }

    override fun setup() {

    }

    override fun draw() {
        while (pExecutor.workQueue.size > 0) {
            pExecutor.workQueue.take().run()
        }
        background(0)
        fill(255f, 255f, 255f)
        movieViews.forEach { it.render() }
    }

    fun movieEvent(m: Movie) {
        m.read()
        movieViews.find { it.hasMovie(m) }?.movieEvent(m)
    }

    override fun addView(v: MovieContract.View) {
        movieViews.add(v)
    }

    companion object {
        val LIB_PATH = "${System.getProperty("user.home")}/Documents/Processing/libraries/video/library/macosx64"

        @JvmStatic
        val appletModule = module {
            single { TestPApplet() }
            single<PApplet> { get<TestPApplet>() }
            single<MovieContract.Sketch> { get<TestPApplet>() }
        }
    }

}