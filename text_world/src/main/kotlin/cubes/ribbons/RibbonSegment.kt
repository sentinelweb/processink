package cubes.ribbons

import processing.core.PApplet
import processing.core.PVector
import java.util.*

/**
 * A bezier curve with a width
 */
class RibbonSegment internal constructor(
    p: PApplet,
    var startPt: PVector,
    var endPt: PVector,
    var controlPt: PVector,
    var ribbonWidth: Float,
    var resolution: Float,
    pcolor: Float
) {
    var stepId = 0
    var ribbonColor = 0f
    var quads: LinkedList<Quad3D>
    var p: PApplet

    init {
        ribbonColor = pcolor
        this.p = p
        quads = LinkedList()
    }

    fun draw() {
        val size = quads.size
        for (i in 0 until size) {
            val q = quads[i]
            q.draw()
        }
    }

    fun removeSegment() {
        if (quads.size > 1) quads.removeFirst()
    }

    fun addSegment() {
        p.fill(ribbonColor, 100f, 100f, 70f)
        var t = stepId / resolution
        val p0 = getOffsetPoint(t, 0f)
        val p3 = getOffsetPoint(t, ribbonWidth)
        stepId++
        if (stepId > resolution) return
        t = stepId / resolution
        val p1 = getOffsetPoint(t, 0f)
        val p2 = getOffsetPoint(t, ribbonWidth)
        Quad3D(p, p0, p1, p2, p3).apply { quads.add(this) }

    }

    /**
     * Given a bezier curve defined by 3 points, an offset distance (k) and a time (t), returns a Point3D
     */
    private fun getOffsetPoint(t: Float, k: Float): PVector {
        val p0 = startPt
        val p1 = controlPt
        val p2 = endPt
        //-- x(t), y(t)
        val xt = (1 - t) * (1 - t) * p0.x + 2 * t * (1 - t) * p1.x + t * t * p2.x
        val yt = (1 - t) * (1 - t) * p0.y + 2 * t * (1 - t) * p1.y + t * t * p2.y
        val zt = (1 - t) * (1 - t) * p0.z + 2 * t * (1 - t) * p1.z + t * t * p2.z
        //-- x'(t), y'(t)
        val xd = t * (p0.x - 2 * p1.x + p2.x) - p0.x + p1.x
        val yd = t * (p0.y - 2 * p1.y + p2.y) - p0.y + p1.y
        val zd = t * (p0.z - 2 * p1.z + p2.z) - p0.z + p1.z
        val dd = PApplet.pow(xd * xd + yd * yd + zd * zd, 1 / 3.toFloat())
        return PVector(xt + k * yd / dd, yt - k * xd / dd, zt - k * xd / dd)
    }
}