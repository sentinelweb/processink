package shader_tut

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PImage
import processing.core.PShape
import processing.opengl.PShader

// https://processing.org/tutorials/pshader/ (Texture+Light shaders)

// combines last 2 examples

// Note that a texlight cannot be used to render a scene only with textures or only with lights,
// in those cases either a texture or light shader will be needed.

fun main(args: Array<String>) {
    ShaderTutorial_4_TexLight().run()
}

private class ShaderTutorial_4_TexLight : PApplet() {

    override fun settings() {
        size(640, 360, PConstants.P3D)
    }

    lateinit var can: PShape
    var angle: Float = 0f
    lateinit var vertTexLightShader: PShader
    lateinit var pixTexLightShader: PShader
    lateinit var label: PImage
    lateinit var currentTexLightShader: PShader
    override fun setup() {
        size(640, 360, P3D)
        vertTexLightShader = loadShader(
            "${BASE_RESOURCES}/shader_tut/vertTexLightFrag_4.glsl",
            "${BASE_RESOURCES}/shader_tut/vertTexLightVert_4.glsl"
        )
        pixTexLightShader = loadShader(
            "${BASE_RESOURCES}/shader_tut/pixelTexLightFrag_4.glsl",
            "${BASE_RESOURCES}/shader_tut/pixelTexLightVert_4.glsl"
        )
        currentTexLightShader = vertTexLightShader
        label = loadImage("${BASE_RESOURCES}/shader_tut/tex_4.jpg")
        can = createCan(100f, 200f, 8)
    }

    override fun draw() {
        background(0)
        shader(currentTexLightShader)
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
        if (currentTexLightShader == vertTexLightShader) {
            currentTexLightShader = pixTexLightShader
            println("Using pixLightShader")
        } else {
            currentTexLightShader = vertTexLightShader
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