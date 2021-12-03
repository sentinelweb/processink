package cubes.objects

import cubes.CubesProcessingView
import cubes.util.pushMatrix
import processing.core.PApplet
import processing.core.PShape

class Terminator constructor(
    override var p: PApplet?,
) : Shape(p) {

    val terminator: PShape

    init {
        terminator = p!!.loadShape("${CubesProcessingView.BASE_RESOURCES}/obj/terminator/terminator.obj")
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
                    shape(terminator)
                }
            }
        }
    }
}
