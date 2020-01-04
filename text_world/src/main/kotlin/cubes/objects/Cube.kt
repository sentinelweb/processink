package cubes.objects

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PShape

class Cube constructor(
    private val p: PApplet,
    val width: Float,
    height: Float = width,
    depth: Float = width
) : Shape(p) {
    private var cubeShape: PShape

    init {
        // todo export to a factory?
        cubeShape = p.createShape(PConstants.BOX, width, height, depth).apply {
            this?.disableStyle()
        }
    }

    fun draw() {
        p.pushMatrix()
        p.translate(position.x, position.y, position.z)
        p.pushMatrix()
        p.rotateX(angle.x)
        p.rotateY(angle.y)
        p.rotateZ(angle.z)
        updateColors()
        p.scale(scale.x, scale.y, scale.z)
        p.shape(cubeShape)
        p.popMatrix()
        p.popMatrix()
    }

    companion object {

    }
}