package cubes.objects

import cubes.CubesProcessingView
import cubes.motion.Motion
import cubes.motion.NoMotion
import cubes.util.pushMatrix
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import processing.core.PApplet
import processing.core.PShape

@Serializable
class MillenuimFalcon constructor(
    @Transient
    override var p: PApplet? = null,
) : Shape(p) {

    @Transient
    var motion: Motion<Shape, Any> = NoMotion()

    @Transient
    private lateinit var falcon: PShape

    init {
        falcon = p!!.loadShape("${CubesProcessingView.BASE_RESOURCES}/obj/millennium-falcon/source/mf.obj")
    }

    fun draw() {
        p?.apply {
            lights()
            pushMatrix {
                translate(position.x, position.y, position.z)
                pushMatrix {
                    super.updateColors()
                    rotateX(angle.x)
                    rotateY(angle.y)
                    rotateZ(angle.z)
                    scale(scale.x, scale.y, scale.z)
                    shape(falcon)
                }
            }
        }
    }

    fun updateState() {
        motion.execute(0, this)
    }
}
