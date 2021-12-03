package cubes.objects

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.robmunro.processing.util.toProcessing
import processing.core.PApplet
import processing.core.PVector
import java.awt.Color

@Serializable
open class Shape constructor(
    @Transient
    open var p: PApplet? = null,
    @Contextual
    val angle: PVector = PVector(),
    @Contextual
    val position: PVector = PVector(),
    @Contextual
    val scale: PVector = PVector(1f, 1f, 1f)
) {
    var fill: Boolean = false
    @Contextual
    var fillColor: Color = Color.WHITE
    var stroke: Boolean = true
    @Contextual
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
        p?.apply {
            if (fill) {
                val fc = fillColor.toProcessing(this)
                fill(red(fc), green(fc), blue(fc), alpha(fc))
            } else {
                noFill()
            }

            if (stroke) {
                stroke(strokeColor.toProcessing(this))
                if (strokeWeight > 0) {
                    strokeWeight(strokeWeight)
                }
            } else {
                noStroke()
            }
        }
    }

    open fun setApplet(applet: PApplet) {
        p = applet
    }

}