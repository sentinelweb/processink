package shader_tut

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PShape
import processing.opengl.PShader


// https://processing.org/tutorials/pshader/ (Point and line shaders 10.4)

fun main(args: Array<String>) {
    ShaderTutorial_10_LineSimple().run()
}

private class ShaderTutorial_10_LineSimple : PApplet() {

    override fun settings() {
        size(640, 360, PConstants.P3D)
    }

    lateinit var cube: PShape
    lateinit var lineShader: PShader
    var angle = 0f

    var weight = 10f


    override fun setup() {

        lineShader = loadShader("${BASE}lineFrag_10.glsl", "${BASE}lineVert_10.glsl")

        cube = createShape(BOX, 150f)
        cube.setFill(false)
        cube.setStroke(color(255))
        cube.setStrokeWeight(weight)
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