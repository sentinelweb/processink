package shader_tut

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PShape
import processing.opengl.PShader

// https://processing.org/tutorials/pshader/ (Light shaders)

// we can make lights look better by replacing the above model, which is vertex-based, by a more
// accurate per-pixel lighting calculation. The idea is to interpolate the normal and direction
// vectors instead of the final color of the vertex, and then calculate the intensity value at each
// fragment by using the normal and direction passed from the vertex shader with varying variables.

// pixLightShader does the lighting interpolation in the fragment shader -  SO SMOOTHER
// vertLightShader just does it for each vertex so its less smooth
fun main(args: Array<String>) {
    ShaderTutorial_3_Light().run()
}

private class ShaderTutorial_3_Light : PApplet() {

    override fun settings() {
        size(640, 360, PConstants.P3D)
    }

    lateinit var can: PShape
    var angle: Float = 0f
    lateinit var vertLightShader: PShader
    lateinit var pixLightShader: PShader
    lateinit var currentLightShader: PShader

    override fun setup() {
        size(640, 360, P3D)
        vertLightShader = loadShader(
            "${BASE_RESOURCES}/shader_tut/vertLightFrag_3.glsl",
            "${BASE_RESOURCES}/shader_tut/vertLightVert_3.glsl"
        )
        pixLightShader = loadShader(
            "${BASE_RESOURCES}/shader_tut/pixelLightFrag_3.glsl",
            "${BASE_RESOURCES}/shader_tut/pixelLightVert_3.glsl"
        )
        currentLightShader = vertLightShader
        can = createCan(100f, 200f, 8)
    }

    override fun draw() {
        background(0)
        shader(currentLightShader)

        pointLight(255f, 255f, 255f, width / 2f, height.toFloat(), 200f)

        translate(width / 2f, height / 2f)
        rotateY(angle)

        shape(can)
        angle += 0.01f
    }

    fun createCan(r: Float, h: Float, detail: Int): PShape {
        textureMode(NORMAL)
        val sh = createShape()
        sh.beginShape(QUAD_STRIP)
        sh.noStroke()

        for (i in 0..detail) {
            angle = TWO_PI / detail
            val x = sin(i * angle)
            val z = cos(i * angle)
            val u = i.toFloat() / detail
            sh.normal(x, 0f, z)
            sh.vertex(x * r, -h / 2, z * r, u, 0f)
            sh.vertex(x * r, +h / 2, z * r, u, 1f)
        }
        sh.endShape()
        return sh
    }

    override fun keyPressed() {
        if (currentLightShader == vertLightShader) {
            currentLightShader = pixLightShader
            println("Using pixLightShader")
        } else {
            currentLightShader = vertLightShader
            println("Using vertLightShader")
        }
    }

    companion object {
        private var BASE_RESOURCES = "${System.getProperty("user.dir")}/first/src/main/resources"
    }

    fun run() {
        runSketch(arrayOf(this::class.java.simpleName), this)
    }
}