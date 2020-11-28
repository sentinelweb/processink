package speecher.generator

import net.robmunro.processing.util.toProcessing
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PFont
import processing.video.Movie
import speecher.generator.bank.MovieBankContract
import speecher.scheduler.ProcessingExecutor
import speecher.util.wrapper.LogWrapper
import java.io.File

class GeneratorView constructor(
    private val pExecutor: ProcessingExecutor,
    private val log: LogWrapper
) : PApplet(), GeneratorContract.View {

    override lateinit var presenter: GeneratorContract.Presenter

    override var bankView: MovieBankContract.View? = null

    private lateinit var f: PFont
    private var subtitleColor: Int = color(255, 255, 255)

    private var recordFrameCount = 0
    private var recordPath: File? = null

    init {
        // https://forum.processing.org/two/discussion/7593/processing-2-2-1-in-maven
        System.setProperty("jna.library.path", "${LIB_PATH}/")
        System.setProperty("gstreamer.library.path", "${LIB_PATH}/")
        System.setProperty("gstreamer.plugin.path", "${LIB_PATH}/plugins/")
        log.tag(this)
    }

    // region Processing
    override fun settings() {
        size(1024, 768, PConstants.P2D)
    }

    override fun setup() {
        background(0)
        setFont("Thonburi", 24f)
        presenter.initialise()
        presenter.selectedFontColor?.let { subtitleColor = it.toProcessing(this) }
    }

    override fun recordNew(path: String) {
        recordPath = File(path)
        recordFrameCount = 0
    }

    override fun recordStop() {
        recordPath = null
        log.d("recorded: $recordFrameCount frames")
    }

    override fun cleanup() {
        bankView?.cleanup()
    }


    override fun draw() {
        while (pExecutor.workQueue.size > 0) {
            pExecutor.workQueue.take().run()
        }
        background(0)
        fill(255f, 255f, 255f)
        //log.d("active = $active")
        //if (active > -1) movieViews[active].render()
        bankView?.render()

        fill(subtitleColor)
        presenter.subtitleToDisplay.let { text(it, width / 2f, height - 25f) }
        recordPath?.apply {
            saveFrame(File(recordPath, "frame_$recordFrameCount").absolutePath)
            recordFrameCount++
        }
    }
    // endregion

    // region movie
    fun movieEvent(m: Movie) {
        m.read()
        bankView?.movieEvent(m)
    }
    // endregion

    // region View
    override fun run() {
        runSketch(arrayOf(this::class.java.simpleName), this)
    }

    override fun openMovie(i: Int, file: File) {
        // todo
    }

    override fun setFont(fontName: String, size: Float) {
        f = createFont(fontName, size)
        textFont(f)
        textSize(size)
        textAlign(CENTER, CENTER)
    }

    override fun updateFontColor() {
        presenter.selectedFontColor?.let { subtitleColor = it.toProcessing(this) }
    }
    // endregion

    companion object {
        val LIB_PATH = "${System.getProperty("user.home")}/Documents/Processing/libraries/video/library/macosx64"

    }


}