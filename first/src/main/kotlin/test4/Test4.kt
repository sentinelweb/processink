package test4

import processing.core.PApplet
import processing.core.PConstants
import processing.opengl.PShader
// https://processing.org/tutorials/pshader/ (2nd last)

fun main(args: Array<String>) {
    Test4().run()
}

class Test4:PApplet() {
    lateinit var pointShader: PShader

    override fun settings() {
        size(640, 360, PConstants.P3D)
    }
    override fun setup() {

        pointShader = loadShader(
            "$BASE_RESOURCES/test4/pointfrag.glsl",
            "$BASE_RESOURCES/test4/pointvert.glsl"
        )
        pointShader.set("sharpness", 0.9f)
        strokeCap(PConstants.SQUARE)
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
        private var BASE_RESOURCES = "${System.getProperty("user.dir")}/first/src/main/resources"
    }

    fun run() {
        runSketch(arrayOf(this::class.java.simpleName), this)
    }
}