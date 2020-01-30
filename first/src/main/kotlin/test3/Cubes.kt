package test3

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PShape
import processing.opengl.PShader


fun main(args: Array<String>) {
    Test3().run()
}

private class Test3 : PApplet() {
    lateinit var lineShader: PShader
    val cubes = mutableListOf<Cubey>()

    override fun settings() {
        size(1920, 1080, PConstants.P3D)
    }

    override fun setup() {
        lineShader = loadShader(
            "$BASE_RESOURCES/test3/linefrag.glsl",
            "$BASE_RESOURCES/test3/linevert.glsl"
        )
        lineShader.set("weight", 20f)
        (0..20).forEach {
            cubes.add(
                Cubey(
                    createShape(PConstants.BOX, (it+2)*25f).apply {
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
//            lineShader.set("weight", mouseX * i / 20f)
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

    companion object {
        private var BASE_RESOURCES = "${System.getProperty("user.dir")}/first/src/main/resources"
    }
}