package cubes.models

import cubes.CubesProcessingView
import cubes.motion.VelocityRotationMotion
import cubes.util.pushMatrix
import cubes.util.set
import processing.core.PApplet
import processing.core.PShape
import java.awt.Color

class Terminator constructor(
    override var p: PApplet?,
) : Shape(p) {

    lateinit var terminator: PShape

    init {
        loadTerminator()
    }

    private fun loadTerminator() {
        terminator = p!!.loadShape("${CubesProcessingView.BASE_RESOURCES}/obj/terminator/terminator.obj")
    }

    override fun setApplet(applet: PApplet) {
        super.setApplet(applet)
        loadTerminator()
    }

    override fun draw() {
        p?.apply {
            lights()
            pushMatrix {
                translate(position.x, position.y, position.z)
                pushMatrix {
                    rotateX(angle.x)
                    rotateY(angle.y)
                    rotateZ(angle.z)
                    scale(scale.x, scale.y, scale.z)
                    updateShapeColors(terminator)
                    shape(terminator)
                }
            }
        }
    }

    companion object {
        fun create(p: PApplet) = Terminator(p)
            .apply { scale.set(6) }
            .apply { position.set(p.width / 2f, p.height.toFloat()) }
            .apply { angle.set(0f, 0f, Math.PI.toFloat()) }
            .apply { fill = true }
            .apply { fillColor = Color.GRAY }
            .apply { motion = VelocityRotationMotion(0.01f, 0.0f, Triple(false, true, false)) }
    }
}
