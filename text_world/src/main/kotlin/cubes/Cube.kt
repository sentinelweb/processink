package cubes

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PShape

class Cube constructor(
    private val p: PApplet,
    val width: Float,
    height: Float = width,
    depth: Float = width,
    var angle: Float = 0f,
    val position: Triple<Float, Float, Float> = Triple(0f, 0f, 0f)
) {
    private var cube: PShape
    init {
        cube = p.createShape(PConstants.BOX, width, height, depth).apply {
            setFill(false)
            setStroke(p.color(255))
            setStrokeWeight(20f)
        }
    }

    fun draw() {
        p.pushMatrix()
        p.rotateX(angle)
        p.rotateY(angle)
        p.shape(cube)
        p.popMatrix()
    }

    companion object {

    }
}