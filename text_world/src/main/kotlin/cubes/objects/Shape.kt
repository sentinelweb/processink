package cubes.objects

import cubes.gui.toProcessing
import processing.core.PApplet
import processing.core.PVector
import java.awt.Color

open class Shape constructor(
    private val p: PApplet,
    val angle: PVector = PVector(),
    val position: PVector = PVector(),
    val scale: PVector = PVector(1f, 1f, 1f)
) {
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

    protected fun updateColors() {
        if (fill) {
            p.fill(fillColor.toProcessing(fillAlpha, p))
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

}