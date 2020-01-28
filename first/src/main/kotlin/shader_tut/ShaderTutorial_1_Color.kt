package shader_tut

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PShape
import processing.opengl.PShader

// https://processing.org/tutorials/pshader/ (Color shaders)

fun main(args: Array<String>) {
    ShaderTutorial().run()
}

class ShaderTutorial : PApplet() {

    override fun settings() {
        size(640, 360, PConstants.P3D)
    }

    lateinit var can: PShape
    var angle: Float = 0f
    lateinit var colorShader: PShader


    override fun setup() {
        size(640, 360, P3D)
        can = createCan(100f, 200f, 32)
        colorShader = loadShader(
            "${BASE_RESOURCES}/shader_tut/colorfrag_1.glsl",
            "${BASE_RESOURCES}/shader_tut/colorvert_1.glsl"
        )
    }

    override fun draw() {
        background(0)
        shader(colorShader)
        translate(width / 2f, height / 2f)
        rotateY(angle)

        shape(can)
        angle += 0.01f
    }

    fun createCan(r: Float, h: Float, detail: Int): PShape {
        textureMode(NORMAL)
        val sh = createShape()
        sh.beginShape(QUAD_STRIP)
        sh.fill(255f, 255f, 0f)
        sh.noStroke()
        for (i in 0..detail - 1) {
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

    companion object {
        private var BASE_RESOURCES = "${System.getProperty("user.dir")}/first/src/main/resources"
    }

    fun run() {
        runSketch(arrayOf(this::class.java.simpleName), this)
    }
}