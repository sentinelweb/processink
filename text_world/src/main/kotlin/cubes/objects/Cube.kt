package cubes.objects

import cubes.gui.toProcessing
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PShape
import processing.core.PVector
import java.awt.Color

class Cube constructor(
    private val p: PApplet,
    val width: Float,
    height: Float = width,
    depth: Float = width,
    var angle: Triple<Float, Float, Float> = Triple(0f, 0f, 0f),
    val position: PVector = PVector(),
    val scale: PVector = PVector()
) {
    private var cubeShape: PShape

    var fill: Boolean = false
    var fillColor: Color = Color.BLUE
    var fillAlpha: Float = 255f
    var stroke: Boolean = true
    var strokeColor: Color = Color.WHITE
    var strokeWeight: Float = 20f
        set(value) {
            field = if (value > 0) value else 1f
            if (value > 0) {// shape seems to die after hitting zero
                this.stroke = true
            } else {
                this.stroke = false
            }
        }

    init {
        cubeShape = p.createShape(PConstants.BOX, width, height, depth).apply {
            disableStyle()
        }
    }

    private fun updateColors() {
        if (fill) {
            p.fill(fillColor.toProcessing(fillAlpha,p))
        } else {
            p.noFill()
        }

        if (stroke) {
            p.stroke(strokeColor.toProcessing(p))
            if (strokeWeight > 0) {
                p.strokeWeight(strokeWeight)
            }
        } else {
            p.noStroke()
        }
    }

    fun draw() {
        p.pushMatrix()
        p.translate(position.x, position.y, position.z)
        p.pushMatrix()
        p.rotateX(angle.first)
        p.rotateY(angle.second)
        p.rotateZ(angle.third)
        updateColors()
        p.scale(scale.x, scale.y, scale.z)
        p.shape(cubeShape)
        p.popMatrix()
        p.popMatrix()
    }

    companion object {

    }
}