package cubes.objects

import cubes.util.toProcessing
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
    var fillColor: Color = Color.WHITE
    var stroke: Boolean = true
    var strokeColor: Color = Color.GRAY
    var strokeWeight: Float = 2f
        set(value) {
            field = if (value > 0) value else 1f
            if (value > 0) {// shape seems to die after hitting zero
                this.stroke = true
            } else {
                this.stroke = false
            }
        }
    var visible = true

    protected fun updateColors() {
        if (fill) {
            val fc = fillColor.toProcessing(p)
            p.fill(p.red(fc), p.green(fc), p.blue(fc), p.alpha(fc))
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