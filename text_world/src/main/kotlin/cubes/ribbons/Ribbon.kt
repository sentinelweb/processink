package cubes.ribbons

import processing.core.PApplet
import processing.core.PApplet.lerp
import processing.core.PVector
import kotlin.random.Random

class Ribbon(
    private val p: PApplet,
    private val ribbonColor: Float,
    private val ribbonWidth: Float,
    private var ribbonTarget: PVector
) {
    // todo parameterise
    private val NUMCURVES = 15 //number of ribbonCurves per ribbon
    private val CURVERESOLUTION = 25 //lower -> faster
    private var NOISESTEP = 0.005f
    private var MAXSEPARATION = 500f

    private val pts: MutableList<PVector> = mutableListOf()
    private val segments: MutableList<RibbonSegment> = mutableListOf()
    private lateinit var currentSegment: RibbonSegment
    private var stepId: Int
    private var ribbonSeparation = 0f
    private var noisePosn: Float = 0f

    init {
        pts.add(randPt)
        pts.add(randPt)
        pts.add(randPt)

        stepId = 0
        addRibbonCurve()
    }

    fun draw() {
        currentSegment.addSegment()
        val size = segments.size
        if (size > NUMCURVES - 1) {
            segments[0].removeSegment()
        }
        ribbonSeparation = lerp(-MAXSEPARATION, MAXSEPARATION, p.noise(NOISESTEP.let { noisePosn += it; noisePosn }))
        stepId++
        if (stepId > CURVERESOLUTION) addRibbonCurve()
        //draw curves
        for (i in 0 until size) {
            segments[i].draw()
        }
    }

    private fun addRibbonCurve() { //add new point
        pts.add(randPt)
        val nextPt = pts.elementAt(pts.size - 1)
        val curPt = pts.elementAt(pts.size - 2)
        val lastPt = pts.elementAt(pts.size - 3)
        val lastMidPt = PVector(
            (curPt.x + lastPt.x) / 2,
            (curPt.y + lastPt.y) / 2,
            (curPt.z + lastPt.z) / 2
        )
        val midPt = PVector(
            (curPt.x + nextPt.x) / 2,
            (curPt.y + nextPt.y) / 2,
            (curPt.z + nextPt.z) / 2
        )
        currentSegment =
            RibbonSegment(p, lastMidPt, midPt, curPt, ribbonWidth, CURVERESOLUTION.toFloat(), ribbonColor).apply {
                segments.add(this)
            }

        //remove old curves
        if (segments.size > NUMCURVES) {
            segments.removeAt(0)
        }
        stepId = 0
    }

    private val randPt: PVector
        get() = PVector(
            ribbonTarget.x + Random.nextFloat() * 2 * ribbonSeparation - ribbonSeparation,
            ribbonTarget.y + Random.nextFloat() * 2 * ribbonSeparation - ribbonSeparation,
            ribbonTarget.z + Random.nextFloat() * 2 * ribbonSeparation - ribbonSeparation
        )
}