package cubes

import processing.core.PApplet
import processing.core.PShape

class Terminator constructor(
    val p:PApplet,
    var ry:Float = 0f
){
    val terminator: PShape

    init {
        terminator = p.loadShape("${Cubes.BASE_RESOURCES}/terminator/terminator.obj")
        terminator.scale(13.0f)
    }

    fun draw() {
        p.lights()
        p.pushMatrix()
        p.fill(200)
        p.translate(p.width / 2f, p.height.toFloat()-120)
        p.rotateZ(PApplet.PI)
        p.rotateY(ry)
        ry += 0.02f
        p.shape(terminator)
        p.popMatrix()
    }
}