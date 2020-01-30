package shader_tut

import processing.core.PApplet
import processing.core.PConstants
import processing.opengl.PShader

// https://processing.org/tutorials/pshader/ (Point and line shaders 10.1)

fun main(args: Array<String>) {
    ShaderTutorial_7_Point().run()
}

private class ShaderTutorial_7_Point : PApplet() {

    override fun settings() {
        size(640, 360, PConstants.P3D)
    }

    lateinit var pointShader: PShader

    override fun setup() {
        pointShader = loadShader("${BASE}pointFrag_7.glsl", "${BASE}pointVert_7.glsl")

        stroke(255)
        strokeWeight(50f)

        background(0)
    }

    override fun draw() {
        shader(pointShader, POINTS)
        if (mousePressed) {
            point(mouseX.toFloat(), mouseY.toFloat())
        }
    }

    companion object {
        private var BASE = "${System.getProperty("user.dir")}/first/src/main/resources/shader_tut/"
    }

    fun run() {
        runSketch(arrayOf(this::class.java.simpleName), this)
    }
}