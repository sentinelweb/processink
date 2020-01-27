package cubes.ribbons
/**
 * BASED ON:
 * BezierRibbons by Felix Turner
 * www.airtightinteractive.com
 * Randomly moving bezier ribbons. Ribbon separation is determined by noise(). Uses OpenGL additive blending.
 * -----------------
 * convert to kotlin by rob munro
 */
import com.jogamp.opengl.GL
import cubes.util.pushMatrix
import processing.core.PApplet
import processing.core.PApplet.map
import processing.core.PConstants.HSB
import processing.core.PConstants.RGB
import processing.core.PVector
import processing.opengl.PJOGL


class Ribbons constructor(
    private val p: PApplet
) {
    var RIBBONCOUNT = 10
    var RIBBONWIDTH = 5f

    lateinit var ribbons: MutableList<Ribbon>

    fun setup() {
        val stageHeight = p.height.toFloat()
        val stageWidth = p.width.toFloat()
        val stageDepth = p.width.toFloat()

        //create ribbons
        ribbons = mutableListOf()
        for (i in 0 until RIBBONCOUNT) {
            ribbons.add(
                Ribbon(
                    p,
                    map(i.toFloat(), 0f, RIBBONCOUNT.toFloat(), 0f, 100f),
                    RIBBONWIDTH,
                    PVector(
                        p.random(-stageWidth / 2, stageWidth / 2),
                        p.random(-stageHeight / 2, stageHeight / 2),
                        p.random(-stageDepth / 2, 0f)
                    )
                )
            )
        }
    }

    fun draw() {
        p.colorMode(HSB, 100f)
        val pgl = p.g.beginPGL() as PJOGL
        pgl.gl.glDisable(GL.GL_DEPTH_TEST)
        pgl.gl.glEnable(GL.GL_BLEND)
        pgl.gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE)
        p.g.endPGL()

        p.pushMatrix {
            p.translate(p.width / 2f, p.height / 2f)
            for (i in 0 until RIBBONCOUNT) {
                val r = ribbons.elementAt(i)
                r.draw()
            }
        }
        p.colorMode(RGB)
    }
}