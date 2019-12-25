package cubes

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PFont
import processing.core.PVector
import java.awt.Point

class TextList constructor(
    private val p: PApplet,
    val texts: MutableList<Text>
) {
    private val f: PFont

    init {
        f = p.createFont("ArialMT", 60f)
    }

    data class Text constructor(
        val text: CharSequence,
        val point: PVector = PVector()
    ) {
        fun draw(p:PApplet){
            p.text(text.toString(), point.x, point.y, point.z)
        }
    }

    fun draw() {
        setProps()
        p.pushMatrix()
        p.translate(p.width / 2f, p.height/2f)
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

    companion object {

    }
}