package cubes.models

import cubes.motion.Motion
import cubes.util.pushMatrix
import cubes.util.set
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PFont
import java.awt.Color
import java.awt.Font

@Serializable
data class TextList constructor(
    @Transient
    override var p: PApplet? = null
) : Shape(p) {

    enum class Mode { SINGLE, ALL }

    enum class Ordering { RANDOM, NEAR_RANDOM, INORDER, REVERSE }

    val texts: MutableList<Text> = mutableListOf()

    @Contextual
    override var fillColor: Color
        get() = super.fillColor
        set(value) {
            super.fillColor = value
            texts.forEach {
                it.fillColor = value
            }
        }

    @Contextual
    override var fill: Boolean
        get() = super.fill
        set(value) {
            super.fill = value
            texts.forEach {
                it.fill = value
            }
        }

    @Transient
    var textMotion: Motion<Text, Any>? = null

    @Transient
    var endFunction: () -> Unit = {}

    var mode: Mode = Mode.SINGLE
    var ordering: Ordering = Ordering.INORDER
    var timeMs: Float = 2000f

    @Transient
    private var pFont: PFont? = null

    @Contextual
    private var javaFont: Font? = null
    private var currentIndex = 0
    private var endWasCalled: Boolean = false
    private var startTime: Long? = null

    init {
        fill = true
        javaFont = javaFont ?: Font.getFont("ArialMT")
        javaFont?.apply {
            pFont = p?.createFont(javaFont?.fontName, 40f)
        }
    }

    override fun setApplet(applet: PApplet) {
        super.setApplet(applet)
        p = applet
        texts.forEach { it.setApplet(applet) }
        javaFont?.apply {
            pFont = p?.createFont(javaFont?.fontName, 40f)
        }
    }

    override fun updateState() {
        motion.execute(0, this)
        textMotion?.run {
            texts.forEachIndexed { i, text ->
                if (thisTextVisible(i)) {
                    execute(i, text)
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

    fun goto(index: Int) {
        currentIndex = if (index < texts.size) index else texts.size - 1
    }

    fun next() {
        selectNextIndex()
    }

    private fun selectNextIndex() {
        p?.apply {
            currentIndex = when (ordering) {
                Ordering.INORDER -> ++currentIndex % texts.size
                Ordering.REVERSE -> if (--currentIndex > -1) currentIndex else texts.size - 1
                Ordering.RANDOM -> random(texts.size.toFloat()).toInt()
                Ordering.NEAR_RANDOM -> (currentIndex + (random(5f) - 2f).toInt()) % texts.size
            }
        }
    }

    fun isEnded() = startTime?.let { System.currentTimeMillis() - it > timeMs } ?: false

    private fun thisTextVisible(i: Int): Boolean {
        return mode == Mode.ALL || (mode == Mode.SINGLE && currentIndex == i)
    }

    fun setProps() {
        p?.apply {
            pFont?.apply { textFont(this) }
            textSize(60f)
            textAlign(PConstants.CENTER, PConstants.CENTER)
        }
    }

    fun addText(s: String): TextList {
        texts.add(Text(p, s))
        return this
    }

    fun start() {
        resetTextsState()
        startTime = System.currentTimeMillis()
        textMotion?.start()
        updateState()
        endWasCalled = false
    }

    private fun resetTextsState() {
        this.fillColor = fillColor
        this.fill = fill
        texts.forEach {
            it.position.set(0)
            it.scale.set(1)
            it.angle.set(0)
        }
    }

    fun stop() {
        startTime = null
    }

    fun setFont(selectedFont: Font) {
        javaFont = selectedFont
        pFont = p?.createFont(selectedFont.fontName, selectedFont.size.toFloat())
    }

    override fun draw() {
        if (visible) {
            setProps()
            updateState()
            p?.apply {
                pushMatrix {
                    translate(width / 2f, height / 2f)
                    texts.forEachIndexed { i, text ->
                        if (thisTextVisible(i)) {
                            text.draw()
                        }
                    }
                }
            }
        }
    }

    @Serializable
    class Text constructor(
        @Transient
        override var p: PApplet? = null,
        private val text: String
    ) : Shape(p) {

        override fun draw() {
            if (visible) {
                p?.apply {
                    pushMatrix {
                        translate(position.x, position.y, position.z)
                        pushMatrix {
                            rotateX(angle.x)
                            rotateY(angle.y)
                            rotateZ(angle.z)
                            updateColors()
                            scale(scale.x, scale.y, scale.z)
                            text(text, 0f, 0f, 0f)
                        }
                    }
                }
            }
        }

        override fun setApplet(applet: PApplet) {
            super.setApplet(applet)
            p = applet
        }
    }
}