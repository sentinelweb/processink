package cubes.ribbons

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PVector

/**
 * A quad defined by 4 3D points.
 */
class Quad3D internal constructor(
    var p: PApplet,
    var p0: PVector,
    var p1: PVector,
    var p2: PVector,
    var p3: PVector
) {
    fun draw() {
        //p.noStroke()
        p.stroke(255)
        //smooth();
        p.beginShape(PConstants.QUAD_STRIP)
        p.vertex(p0.x, p0.y, p0.z)
        p.vertex(p3.x, p3.y, p3.z)
        p.vertex(p1.x, p1.y, p1.z)
        p.vertex(p2.x, p2.y, p2.z)
        p.endShape()
    }

}