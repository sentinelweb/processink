package bookofshaders

import processing.core.PApplet
import processing.core.PConstants
import processing.opengl.PShader
import java.awt.Color

fun main() {
    BookOfShadersTest().run()
}
class BookOfShadersTest : PApplet() {

    private lateinit var  shader: PShader

    override fun settings() {
        size(720, 720, PConstants.P3D)
    }

    override fun setup() {
        noStroke()

        shader = loadShader("$BASE_RESOURCES/bookofshaders/8_motionRotationFrag.glsl")
    }

    override fun  draw() {
//        val c = Color.decode("#3949ab")
        val c = Color.BLACK
        background(c.red.toFloat(),c.green.toFloat(),c.blue.toFloat())
        shader.set("u_resolution", width.toFloat(), height.toFloat())
        shader.set("u_mouse", mouseX.toFloat(), mouseY.toFloat())
        shader.set("u_time", millis() / 1000.0f)
        shader(shader)
        rect(0f,0f,width.toFloat(),height.toFloat())
    }

    fun run() {
        runSketch(arrayOf(this::class.java.simpleName), this)
    }

    companion object {
        internal var BASE_RESOURCES = "${System.getProperty("user.dir")}/text_world/src/main/resources"
    }
}