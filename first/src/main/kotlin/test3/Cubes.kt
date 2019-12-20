package test3

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PShape
import processing.opengl.PShader


fun main(args: Array<String>) {
    Test3().run()
}

class Test3 : PApplet() {
    lateinit var lineShader: PShader
    val cubes = mutableListOf<Cubey>()
    override fun settings() {
        size(640, 360, PConstants.P3D)
    }

    override fun setup() {
        lineShader = loadShader(
            "/Users/robmunro/repos/personal/processink/first/src/main/resources/test3/linefrag.glsl",
            "/Users/robmunro/repos/personal/processink/first/src/main/resources/test3/linevert.glsl"
        )
        lineShader.set("weight", 20f)
        (0..20).forEach {
            cubes.add(
                Cubey(
                    createShape(PConstants.BOX, it*10f).apply {
                        setFill(false)
                        setStroke(color(255))
                        setStrokeWeight(1f)
                    })
            )
        }
        hint(PConstants.DISABLE_DEPTH_MASK)
    }

    override fun draw() {
        background(0)
        translate(width / 2f, height / 2f)
        cubes.forEachIndexed() { i, cubey ->
            pushMatrix()
            rotateX(cubey.angle)
            rotateY(cubey.angle)
            lineShader.set("weight", mouseX * i / 150f)
            shader(lineShader, PConstants.LINES)
            shape(cubey.cube)
            cubey.angle += 0.001f * i
            popMatrix()
        }
    }

    data class Cubey(
        val cube: PShape,
        var angle: Float = 0f
    )

    fun run() {
        runSketch(arrayOf(this::class.java.simpleName), this)
    }
}