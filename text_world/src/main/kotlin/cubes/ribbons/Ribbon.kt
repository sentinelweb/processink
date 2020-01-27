package cubes.ribbons

import processing.core.PApplet
import java.util.*

class Ribbon(
    private val p: PApplet,
    private val ribbonColor: Float,
    private val ribbonWidth: Float,
    private var ribbonTarget: Point3D
) {
    private val NUMCURVES = 15 //number of ribbonCurves per ribbon
    private val CURVERESOLUTION = 25 //lower -> faster
    private val pts: Vector<Point3D> = Vector()
    private val curves: LinkedList<RibbonCurve> = LinkedList()
    lateinit var currentCurve: RibbonCurve
    var stepId: Int
    var ribbonSeparation = 0f

    init {
        pts.addElement(randPt)
        pts.addElement(randPt)
        pts.addElement(randPt)
        stepId = 0
        addRibbonCurve()
    }

    fun draw() {
        currentCurve.addSegment()
        val size = curves.size
        if (size > NUMCURVES - 1) {
            curves[0].removeSegment()
        }
        stepId++
        if (stepId > CURVERESOLUTION) addRibbonCurve()
        //draw curves
        for (i in 0 until size) {
            curves[i].draw()
        }
    }

    fun addRibbonCurve() { //add new point
        pts.addElement(randPt)
        val nextPt = pts.elementAt(pts.size - 1) as Point3D
        val curPt = pts.elementAt(pts.size - 2) as Point3D
        val lastPt = pts.elementAt(pts.size - 3) as Point3D
        val lastMidPt = Point3D(
            (curPt.x + lastPt.x) / 2,
            (curPt.y + lastPt.y) / 2,
            (curPt.z + lastPt.z) / 2
        )
        val midPt = Point3D(
            (curPt.x + nextPt.x) / 2,
            (curPt.y + nextPt.y) / 2,
            (curPt.z + nextPt.z) / 2
        )
        currentCurve =
            RibbonCurve(p, lastMidPt, midPt, curPt, ribbonWidth, CURVERESOLUTION.toFloat(), ribbonColor).apply {
                curves.add(this)
            }

        //remove old curves
        if (curves.size > NUMCURVES) {
            curves.removeFirst()
        }
        stepId = 0
    }

    val randPt: Point3D
        get() = Point3D(
            ribbonTarget.x + p.random(-ribbonSeparation, ribbonSeparation),
            ribbonTarget.y + p.random(-ribbonSeparation, ribbonSeparation),
            ribbonTarget.z + p.random(-ribbonSeparation, ribbonSeparation)
        )
}