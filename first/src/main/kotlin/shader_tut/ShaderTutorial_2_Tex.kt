package shader_tut

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PImage
import processing.core.PShape
import processing.opengl.PShader

// https://processing.org/tutorials/pshader/ (Texture shaders)

fun main(args: Array<String>) {
    ShaderTutorial_2_Tex().run()
}

class ShaderTutorial_2_Tex : PApplet() {

    override fun settings() {
        size(640, 360, PConstants.P3D)
    }

    lateinit var can: PShape
    var angle: Float = 0f
    lateinit var texShader: PShader
    lateinit var label: PImage

    override fun setup() {
        size(640, 360, P3D)
        texShader = loadShader(
            "${BASE_RESOURCES}/shader_tut/textFrag_2.glsl",
            "${BASE_RESOURCES}/shader_tut/textVert_2.glsl"
        )
        label = loadImage("${BASE_RESOURCES}/shader_tut/tex.jpg")
        can = createCan(100f, 200f, 32)
    }

    override fun draw() {
        background(0)
        shader(texShader)
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
        sh.texture(label)
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