package shader_tut

import processing.core.PApplet
import processing.core.PConstants
import processing.opengl.PShader


// https://processing.org/tutorials/pshader/ (Point and line shaders 10.3)

fun main(args: Array<String>) {
    ShaderTutorial_9_PointFragment().run()
}

private class ShaderTutorial_9_PointFragment : PApplet() {

    override fun settings() {
        size(640, 360, PConstants.P3D)
    }

    lateinit var pointShader: PShader

    override fun setup() {

        pointShader = loadShader("${BASE}pointFrag_9.glsl", "${BASE}pointVert_9.glsl")
        pointShader.set("sharpness", 0.9f)

        strokeCap(SQUARE)
        background(0)
    }

    override fun draw() {
        if (mousePressed) {
            shader(pointShader, PConstants.POINTS)
            val w = random(5f, 50f)
            pointShader["weight"] = w
            strokeWeight(w)
            stroke(random(255f), random(255f), random(255f))
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