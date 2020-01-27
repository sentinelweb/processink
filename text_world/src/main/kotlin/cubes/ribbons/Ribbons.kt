package cubes.ribbons

/**
 * BASED ON
 * BezierRibbons by Felix Turner
 * www.airtightinteractive.com
 * Randomly moving bezier ribbons. Ribbon separation is determined by noise(). Uses OpenGL additive blending.
 * Press 'S' key to toggle saving images. Use mouse to move camera.
 * Uses the Obsessive Camera Direction Library for Processing: http://www.cise.ufl.edu/~kdamkjer/processing/libraries/ocd/
 */
import cubes.util.pushMatrix
import processing.core.PApplet
import processing.core.PApplet.lerp
import processing.core.PApplet.map
import processing.core.PConstants.HSB


class Ribbons constructor(
    private val p: PApplet
) {
    var RIBBONCOUNT = 10
    var RIBBONWIDTH = 5f
    var NOISESTEP = 0.005f
    var MAXSEPARATION = 500f

    var ribbonSeparation = 0f
    var noisePosn: Float = 0f
    lateinit var ribbons: MutableList<Ribbon>

    fun setup() {
        val stageHeight = p.height.toFloat()
        val stageWidth = p.width.toFloat()
        val stageDepth = p.width.toFloat()
        noisePosn = 0f
        ribbonSeparation = 0f

        p.colorMode(HSB, 100f)

        //create ribbons
        ribbons = mutableListOf()
        for (i in 0 until RIBBONCOUNT) {
            ribbons.add(
                Ribbon(
                    p,
                    map(i.toFloat(), 0f, RIBBONCOUNT.toFloat(), 0f, 100f),
                    RIBBONWIDTH,
                    Point3D(
                        p.random(-stageWidth, stageWidth),
                        p.random(-stageHeight, stageHeight),
                        p.random(-stageDepth, stageDepth)
                    )
                )
            )
        }
    }

    fun draw() {
        p.pushMatrix {
            p.translate(p.width / 2f, p.height / 2f)
            for (i in 0 until RIBBONCOUNT) {
                val r = ribbons.elementAt(i)
                r.ribbonSeparation =
                    lerp(-MAXSEPARATION, MAXSEPARATION, p.noise(NOISESTEP.let { noisePosn += it; noisePosn }))
                r.draw()
            }
        }
    }
}