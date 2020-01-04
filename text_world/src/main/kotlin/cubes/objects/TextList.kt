package cubes.objects

import cubes.gui.toProcessing
import cubes.motion.Motion
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PFont
import java.awt.Font

class TextList constructor(
    private val p: PApplet
) : Shape(p) {

    enum class Mode {
        SINGLE, ALL
    }

    enum class Ordering {
        RANDOM, NEAR_RANDOM, INORDER, REVERSE
    }

    val texts: MutableList<Text> = mutableListOf()
    var motion: Motion<Text>? = null
    var endFunction: () -> Unit = {}

    var mode: Mode = Mode.SINGLE
    var ordering: Ordering = Ordering.INORDER
    var timeMs: Float = 2000f

    private var f: PFont
    private var currentIndex = 0
    private var endWasCalled: Boolean = false
    private var startTime: Long? = null

    init {
        f = p.createFont("ArialMT", 40f)
    }

    fun updateState() {
        motion?.run {
            texts.forEachIndexed { i, text ->
                if (thisTextVisible(i)) {
                    if (!isEnded()) {
                        updateState(i, text)
                    } else {
                        callEndOnce()
                    }
                }
            }
        }
        if (isEnded() && !endWasCalled) {
            selectNextIndex()
            endWasCalled = true
            endFunction()
        }
    }

    fun currentIndex() = currentIndex
    fun currentText() = texts[currentIndex]

    private fun selectNextIndex() {
        currentIndex = when (ordering) {
            Ordering.INORDER -> ++currentIndex % texts.size
            Ordering.REVERSE -> --currentIndex % texts.size // todo check
            Ordering.RANDOM -> p.random(texts.size.toFloat()).toInt()
            Ordering.NEAR_RANDOM -> (currentIndex + (p.random(5f) - 2f).toInt()) % texts.size
        }
    }

    fun isEnded() = startTime?.let { System.currentTimeMillis() - it > timeMs } ?: true

    private fun thisTextVisible(i: Int): Boolean {
        return mode == Mode.ALL || (mode == Mode.SINGLE && currentIndex == i)
    }

    fun draw() {
        setProps()
        updateState()
        p.pushMatrix()
        p.translate(p.width / 2f, p.height / 2f)
        texts.forEachIndexed { i, text ->
            if (thisTextVisible(i)) {
                text.draw(p)
            }
        }
        p.popMatrix()
    }

    fun setProps() {
        p.textFont(f)
        p.textSize(60f)
        p.textAlign(PConstants.CENTER, PConstants.CENTER)
        //textMode(PConstants.SHAPE)
    }

    fun addText(s: String): TextList {
        texts.add(Text(s))
        return this
    }

    fun visible(v: Boolean) {
        texts.forEach { it.visible = v }
    }

    fun start() {
        startTime = System.currentTimeMillis()
        endWasCalled = false
    }

    fun stop() {
        startTime = null
    }

    fun scatterText(low: Float, high: Float) {
        texts.forEach {
            it.position.set(
                p.random(low, high),
                p.random(low, high),
                0f
            )
        }
    }

    fun setFont(selectedFont: Font) {
        f = p.createFont(selectedFont.fontName, selectedFont.size.toFloat())
    }

    inner class Text constructor(
        val text: CharSequence
    ) : Shape(p) {

        fun draw(p: PApplet) {
            if (visible) {
                p.pushMatrix()
                p.translate(position.x, position.y, position.z)
                p.pushMatrix()
                p.rotateX(angle.x)
                p.rotateY(angle.y)
                p.rotateZ(angle.z)
                updateColors()
                val pc = fillColor.toProcessing(p)
                //println("textfill: $fillColor -> ${p.red(pc)},${p.green(pc)},${p.blue(pc)},${p.alpha(pc)} $fill")
                //p.fill(255f,0f,255f,255f)
                p.scale(scale.x, scale.y, scale.z)
                p.text(text.toString(), 0f, 0f, 0f)
                p.popMatrix()
                p.popMatrix()
            }
        }
    }


}