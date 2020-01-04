package cubes.objects

import cubes.gui.toProcessing
import cubes.motion.Motion
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PFont

class TextList constructor(
    private val p: PApplet
) {
    private val f: PFont
    val texts: MutableList<Text> = mutableListOf()
    var motion: Motion<Text>? = null

    init {
        f = p.createFont("ArialMT", 60f)
    }

    fun updateState() {
        motion?.run {
            texts.forEachIndexed { i, cube ->
                if (!isEnded()) {
                    updateState(i, cube)
                } else {
                    callEndOnce()
                }
            }
        }
    }

    fun draw() {
        setProps()
        updateState()
        p.pushMatrix()
        p.translate(p.width / 2f, p.height / 2f)
        texts.forEach {
            it.draw(p)
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
                println("textfill: $fillColor -> ${p.red(pc)},${p.green(pc)},${p.blue(pc)},${p.alpha(pc)} $fill")
                //p.fill(255f,0f,255f,255f)
                p.scale(scale.x, scale.y, scale.z)
                p.text(text.toString(), 0f, 0f, 0f)
                p.popMatrix()
                p.popMatrix()
            }
        }
    }

    fun setFill(fill: Boolean) {
        texts.forEach { it.fill = fill }
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
}