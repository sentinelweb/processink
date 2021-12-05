package bookofshaders

import processing.core.PApplet
import processing.core.PConstants
import processing.opengl.PShader
import java.awt.Color

fun main() {
    BookOfShadersTest().run()
}

class BookOfShadersTest : PApplet() {

    private val shaders = listOf(
        //"st_cloudsNebulaFrag.glsl",
        "st_coldFlameFrag.glsl",
        "st_embersFrag.glsl",
        "st_neon.glsl",
        "st_oceanFrag.glsl",
        "st_refractionPatternFrag.glsl",
//        "st_spaceGif_edit.glsl",
        "st_spaceGif_orig.glsl",
        "st_starField.glsl",
        "st_starField_orig.glsl",
        // "7_circleFrag.glsl",
        //"7_distanceFieldFrag.glsl",
        //"7_frameFrag.glsl",
        //"7_nGon.glsl",
        //"7_polarFrag.glsl",
        //"8_motionRotationFrag.glsl",
    )
    private var index = 0;
    private lateinit var shader: PShader

    override fun settings() {
        size(1280, 720, PConstants.P3D)
    }

    override fun setup() {
        noStroke()
        load()
    }

    private fun load() {
        val name = shaders[index]
        shader = loadShader("$BASE_RESOURCES/shadertoy/$name")
        println("shader = $name")
    }

    override fun draw() {
        val c = Color.BLACK
        background(c.red.toFloat(), c.green.toFloat(), c.blue.toFloat())
        shader.set("u_resolution", width.toFloat(), height.toFloat())
        shader.set("u_mouse", mouseX.toFloat(), mouseY.toFloat())
        shader.set("u_time", millis() / 1000f)
        shader(shader)
        rect(0f, 0f, width.toFloat(), height.toFloat())
    }

    fun run() {
        runSketch(arrayOf(this::class.java.simpleName), this)
    }

    override fun keyPressed() {
        super.keyPressed()
        if (key == ENTER) {
            index++
            index = index % shaders.size
            load()
        }
    }

    companion object {
        internal var BASE_RESOURCES = "${System.getProperty("user.dir")}/text_world/src/main/resources"
    }
}