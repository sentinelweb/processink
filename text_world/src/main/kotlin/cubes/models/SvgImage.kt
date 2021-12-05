package cubes.models

import cubes.CubesProcessingView.Companion.BASE_RESOURCES
import cubes.motion.VelocityRotationMotion
import cubes.util.pushMatrix
import cubes.util.set
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import processing.core.PApplet
import processing.core.PShape
import java.awt.Color

@Serializable
class SvgImage(
    @Transient
    override var p: PApplet? = null,
    val name: String
) : Shape(p) {

    @Transient
    private lateinit var shape: PShape

    init {
        load()
    }

    override fun setApplet(applet: PApplet) {
        super.setApplet(applet)
        load()
    }

    private fun load() {
        shape = p!!.loadShape("${BASE_RESOURCES}/svg/$name")
    }

    override fun draw() {
        p?.apply {
            pushMatrix {
                translate(position.x, position.y, position.z)
                pushMatrix {
                    rotateX(angle.x)
                    rotateY(angle.y)
                    rotateZ(angle.z)
                    scale(scale.x, scale.y, scale.z)
                    updateShapeColors(shape)
                    drawMe(shape)
                }
            }
        }
    }

    private fun drawMe(shape: PShape) {
        val size = 100f
        (shape.getWidth() / shape.getHeight() * size)
            .let { p!!.shape(shape, -it / 2f, -size / 2f, it, size) }
    }

    companion object {
        fun create(p: PApplet, name: String) = SvgImage(p, name)
            .apply { scale.set(2) }
            .apply { position.set(p.width / 2f, p.height / 2f, 0f) }
            .apply { fill = true }
            .apply { fillColor = Color.YELLOW }
            .apply { stroke = false }
            .apply { strokeColor = Color.RED }
            .apply { motion = VelocityRotationMotion(0.01f, 0.0f, Triple(false, true, false)) }
    }
}


