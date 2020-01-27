package cubes.ribbons

import processing.core.PApplet
import processing.core.PConstants

/**
 * A quad defined by 4 3D points.
 */
class Quad3D internal constructor(
    var p: PApplet,
    var p0: Point3D,
    var p1: Point3D,
    var p2: Point3D,
    var p3: Point3D
) {
    fun draw() {
        p.noStroke()
        //smooth();
        p.beginShape(PConstants.QUAD_STRIP)
        p.vertex(p0.x, p0.y, p0.z)
        p.vertex(p3.x, p3.y, p3.z)
        p.vertex(p1.x, p1.y, p1.z)
        p.vertex(p2.x, p2.y, p2.z)
        p.endShape()
    }

}