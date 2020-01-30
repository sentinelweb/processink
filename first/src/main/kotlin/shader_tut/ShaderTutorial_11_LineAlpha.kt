package shader_tut

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PShape
import processing.opengl.PShader

// https://processing.org/tutorials/pshader/ (Point and line shaders 10.5)

fun main(args: Array<String>) {
    ShaderTutorial_11_LineAlpha().run()
}

private class ShaderTutorial_11_LineAlpha : PApplet() {

    lateinit var cube: PShape

    lateinit var lineShader: PShader
    var angle = 0f
    var weight = 20f

    override fun settings() {
        size(640, 360, PConstants.P3D)
    }

    override fun setup() {
        lineShader = loadShader("${BASE}lineFrag_11.glsl", "${BASE}lineVert_11.glsl")
        lineShader.set("weight", weight)

        cube = createShape(BOX, 150f)
        cube.setFill(false)
        cube.setStroke(color(255))
        cube.setStrokeWeight(weight)

        // The DISABLE_DEPTH_MASK hint in the sketch code is a simple (but not perfect) trick to avoid obvious
        // visual artifacts due to the use of semi-transparent geometry together with alpha blending.
        hint(DISABLE_DEPTH_MASK)
    }

    override fun draw() {
        if (mousePressed) {
            background(0)

            translate(width / 2f, height / 2f)
            rotateX(angle)
            rotateY(angle)

            shader(lineShader, LINES)
            shape(cube)

            angle += 0.01f
        }
    }

    companion object {
        private var BASE = "${System.getProperty("user.dir")}/first/src/main/resources/shader_tut/"
    }

    fun run() {
        runSketch(arrayOf(this::class.java.simpleName), this)
    }
}