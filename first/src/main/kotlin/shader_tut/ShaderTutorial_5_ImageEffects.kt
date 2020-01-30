package shader_tut

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PImage
import processing.core.PShape
import processing.opengl.PShader

// https://processing.org/tutorials/pshader/ (Image post-processing effects)

fun main(args: Array<String>) {
    ShaderTutorial_5_ImageEffects().run()
}

class ShaderTutorial_5_ImageEffects : PApplet() {

    override fun settings() {
        size(640, 360, PConstants.P3D)
    }

    lateinit var can: PShape
    var angle: Float = 0f
    lateinit var bwShader: PShader
    lateinit var edgeShader: PShader
    lateinit var embossShader: PShader
    lateinit var currentShader: PShader
    lateinit var label: PImage

    override fun setup() {
        size(640, 360, P3D)
        bwShader = loadShader("${BASE_RESOURCES}/shader_tut/imgBwFrag_5.glsl")
        edgeShader = loadShader("${BASE_RESOURCES}/shader_tut/imgEdgeFrag_5.glsl")
        embossShader = loadShader("${BASE_RESOURCES}/shader_tut/imgEmbossFrag_5.glsl")
        currentShader = bwShader
        label = loadImage("${BASE_RESOURCES}/shader_tut/tex_4.jpg")
        can = createCan(100f, 200f, 8)
    }

    override fun draw() {
        background(0)
        shader(currentShader)
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
        currentShader = when (currentShader) {
            bwShader -> edgeShader
            edgeShader -> embossShader
            embossShader -> bwShader
            else -> bwShader
        }
    }

    companion object {
        private var BASE_RESOURCES = "${System.getProperty("user.dir")}/first/src/main/resources"
    }

    fun run() {
        runSketch(arrayOf(this::class.java.simpleName), this)
    }
}