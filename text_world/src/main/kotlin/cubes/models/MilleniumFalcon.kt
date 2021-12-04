package cubes.models

import cubes.CubesProcessingView
import cubes.motion.VelocityRotationMotion
import cubes.util.pushMatrix
import cubes.util.set
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import processing.core.PApplet
import processing.core.PShape
import java.awt.Color

@Serializable
class MilleniumFalcon constructor(
    @Transient
    override var p: PApplet? = null,
) : Shape(p) {

    @Transient
    private lateinit var falcon: PShape

    init {
        loadFalcon()
    }

    private fun loadFalcon() {
        falcon = p!!.loadShape("${CubesProcessingView.BASE_RESOURCES}/obj/millennium-falcon/mf.obj")
    }

    override fun setApplet(applet: PApplet) {
        super.setApplet(applet)
        loadFalcon()
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
                    updateShapeColors(falcon)
                    shape(falcon)
                }
            }
        }
    }

    companion object {
        fun create(p: PApplet) =
            MilleniumFalcon(p)
                .apply { scale.set(5f) }
                .apply { position.set(p.width / 2f, p.height * 4f / 5, 50f) }
                .apply { angle.set(0f, 0f, -Math.PI.toFloat() / 2) }
                .apply { fillColor = Color.GRAY }
                .apply { fill = true }
                .apply { motion = VelocityRotationMotion(0.01f, 0.0f, Triple(false, false, true)) }
    }
}
