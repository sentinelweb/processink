package shader_tut

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PImage
import processing.core.PShape
import processing.opengl.PShader

// https://processing.org/tutorials/pshader/ (Putting all together)

fun main(args: Array<String>) {
    ShaderTutorial_6_AllTogether().run()
}

private class ShaderTutorial_6_AllTogether : PApplet() {

    override fun settings() {
        size(480, 480, PConstants.P3D)
    }

    var canSize = 60f
    lateinit var label: PImage
    lateinit var can: PShape
    lateinit var cap: PShape
    var angle = 0f

    var useLight = false
    var useTexture = false

    lateinit var selShader: PShader
    lateinit var selShader2: PShader

    lateinit var colorShader: PShader
    lateinit var lightShader: PShader
    lateinit var texShader: PShader
    lateinit var texlightShader: PShader
    lateinit var pixlightShader: PShader
    lateinit var texlightxShader: PShader
    lateinit var bwShader: PShader
    lateinit var edgesShader: PShader
    lateinit var embossShader: PShader

    override fun setup() {
        label = loadImage("${BASE}tex_4.jpg")
        can = createCan(canSize, 2 * canSize, 32, label)
        cap = createCap(canSize, 32)

        colorShader = loadShader("${BASE}colorfrag_1.glsl", "${BASE}colorvert_1.glsl")

        texShader = loadShader("${BASE}textFrag_2.glsl", "${BASE}textVert_2.glsl")

        lightShader = loadShader("${BASE}vertLightFrag_3.glsl", "${BASE}vertLightVert_3.glsl")
        pixlightShader = loadShader("${BASE}pixelLightFrag_3.glsl", "${BASE}pixelLightVert_3.glsl")

        texlightShader = loadShader("${BASE}vertTexLightFrag_4.glsl", "${BASE}vertTexLightVert_4.glsl")
        texlightxShader = loadShader("${BASE}pixelTexLightFrag_4.glsl", "${BASE}pixelTexLightVert_4.glsl")

        bwShader = loadShader("${BASE}imgBwFrag_5.glsl")
        edgesShader = loadShader("${BASE}imgEdgeFrag_5.glsl")
        embossShader = loadShader("${BASE}imgEmbossFrag_5.glsl")

        selShader = texlightShader
        selShader2 = embossShader
        useLight = true
        useTexture = true
        println("Vertex lights, texture shading")
    }

    override fun draw() {
        background(0)
        var x = (1.88 * canSize).toFloat()
        var y = 2 * canSize
        var n = 0
        for (i in 0..2) {
            for (j in 0..2) {
                drawCan(x, y, angle, n)
                x += 2 * canSize + 8
                n++
            }
            x = (1.88 * canSize).toFloat()
            y += 2 * canSize + 5
        }
        angle += 0.01f
    }

    fun drawCan(centerx: Float, centery: Float, rotAngle: Float, i: Int) {
        pushMatrix()
        if (useLight) {
            pointLight(255f, 255f, 255f, centerx, centery, 200f)
        }
        shader(if (i % 2 == 0) selShader else selShader2)
        translate(centerx, centery, 65f)
        rotateY(rotAngle)
        if (useTexture) {
            can.setTexture(label)
        } else {
            can.setTexture(null)
        }
        shape(can)
        noLights()
        resetShader()
        pushMatrix()
        translate(0f, canSize - 5, 0f)
        shape(cap)
        popMatrix()
        pushMatrix()
        translate(0f, -canSize + 5, 0f)
        shape(cap)
        popMatrix()
        popMatrix()
    }

    fun createCan(r: Float, h: Float, detail: Int, tex: PImage): PShape {
        textureMode(NORMAL)
        val sh = createShape()
        sh.beginShape(QUAD_STRIP)
        sh.fill(255f, 255f, 0f)
        sh.setFill(true)
        sh.noStroke()
        sh.texture(tex)
        for (i in 0..detail) {
            val angle = TWO_PI / detail
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

    fun createCap(r: Float, detail: Int): PShape {
        val sh = createShape()
        sh.beginShape(PConstants.TRIANGLE_FAN)
        sh.noStroke()
        sh.fill(128)
        sh.vertex(0f, 0f, 0f)
        for (i in 0..detail) {
            val angle = PConstants.TWO_PI / detail
            val x = sin(i * angle)
            val z = cos(i * angle)
            sh.vertex(x * r, 0f, z * r)
        }
        sh.endShape()
        return sh
    }

    override fun keyPressed() {
        if (key == '1') {
            println("No lights, no texture shading")
            selShader = colorShader
            useLight = false
            useTexture = false
        } else if (key == '2') {
            println("Vertex lights, no texture shading")
            selShader = lightShader
            useLight = true
            useTexture = false
        } else if (key == '3') {
            println("No lights, texture shading")
            selShader = texShader
            useLight = false
            useTexture = true
        } else if (key == '4') {
            println("Vertex lights, texture shading")
            selShader = texlightShader
            useLight = true
            useTexture = true
        } else if (key == '5') {
            println("Pixel lights, no texture shading")
            selShader = pixlightShader
            useLight = true
            useTexture = false
        } else if (key == '6') {
            println("Pixel lights, texture shading")
            selShader = texlightxShader
            useLight = true
            useTexture = true
        } else if (key == '7') {
            println("Black&white texture filtering")
            selShader = bwShader
            useLight = false
            useTexture = true
        } else if (key == '8') {
            println("Edge detection filtering")
            selShader = edgesShader
            useLight = false
            useTexture = true
        } else if (key == '9') {
            println("Emboss filtering")
            selShader = embossShader
            useLight = false
            useTexture = true
        }
    }

    companion object {
        private var BASE = "${System.getProperty("user.dir")}/first/src/main/resources/shader_tut/"
    }

    fun run() {
        runSketch(arrayOf(this::class.java.simpleName), this)
    }
}