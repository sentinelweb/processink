package cubes.models

import cubes.util.pushMatrix
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.robmunro.processing.util.encodeARGB
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PShape

@Serializable
data class Circle constructor(
    @Transient
    override var p: PApplet? = null,
    val width: Float,
    val height: Float = width,
    val depth: Float = width
) : Shape(p) {

    @Transient
    private var cubeShape: PShape? = null

    init {
        initialise()
    }

    override fun setApplet(applet: PApplet) {
        super.setApplet(applet)
        p = applet
        initialise()
    }

    fun initialise() {
        cubeShape = p?.createShape(PConstants.ELLIPSE, 1f, 1f, width, height)
            .apply { this?.disableStyle() }
    }

    override fun draw() {
        p?.apply {
            pushMatrix {
                translate(position.x, position.y, position.z)
                pushMatrix {
                    rotateX(angle.x)
                    rotateY(angle.y)
                    rotateZ(angle.z)
                    updateColors()
                    scale(scale.x, scale.y, scale.z)
                    shape(cubeShape)
                }
            }
        }
    }

    fun log() {
        println(
            "cube: v:$visible - pos:$position - angle:$angle - scale:$scale" +
                    " fill:$fill - fillColor:${fillColor.encodeARGB()} - stroke:$stroke - strokeColor:${strokeColor.encodeARGB()}"
        )
    }

}