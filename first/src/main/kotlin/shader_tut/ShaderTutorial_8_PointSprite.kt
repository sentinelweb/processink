package shader_tut

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PImage
import processing.opengl.PShader


// https://processing.org/tutorials/pshader/ (Point and line shaders 10.2)

fun main(args: Array<String>) {
    ShaderTutorial_8_PointSprite().run()
}

private class ShaderTutorial_8_PointSprite : PApplet() {

    override fun settings() {
        size(640, 360, PConstants.P3D)
    }

    lateinit var pointShader: PShader
    lateinit var cloud1: PImage
    lateinit var cloud2: PImage
    lateinit var cloud3: PImage

    var weight = 100f
    var useShader = true

    override fun setup() {

        pointShader = loadShader("${BASE}spriteFrag_8.glsl", "${BASE}spriteVert_8.glsl")
        pointShader.set("weight", weight)
        cloud1 = loadImage("${BASE}spriteCloud1_8.png")
        cloud2 = loadImage("${BASE}spriteCloud2_8.png")
        cloud3 = loadImage("${BASE}spriteCloud3_8.png")
        pointShader.set("sprite", cloud1)

        strokeWeight(weight)
        strokeCap(SQUARE)
        stroke(255f, 70f)

        background(0)
    }

    override fun draw() {
        if (useShader) {
            shader(pointShader, POINTS)
        } else resetShader(POINTS) // thats it !!

        if (mousePressed) {
            point(mouseX.toFloat(), mouseY.toFloat())
        }
    }

    override fun keyPressed() {
        if (key == '1') {
            pointShader["sprite"] = cloud1
        } else if (key == '2') {
            pointShader["sprite"] = cloud2
        } else if (key == '3') {
            pointShader["sprite"] = cloud3
        } else if (key == '4') {
            background(0)
        } else if (key == '5') {
            useShader = !useShader
        }
    }

    companion object {
        private var BASE = "${System.getProperty("user.dir")}/first/src/main/resources/shader_tut/"
    }

    fun run() {
        runSketch(arrayOf(this::class.java.simpleName), this)
    }
}