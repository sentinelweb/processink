package cubes.shaders

import cubes.util.pushMatrix
import processing.core.*
import processing.opengl.PShader
import java.awt.Color
import java.awt.Point

abstract class ShaderWrapper(
    protected val p: PApplet,
    fragPath: String,
    vertPath: String? = null,
    dimOverride: Point? = null
) {
    var resolution: Point
        private set

    var mouse: Point? = null
    var color: Color? = null

    open protected val type: Int? = PConstants.TRIANGLES // POINTS, LINES, or TRIANGLES

    init {
        resolution = dimOverride ?: Point(p.width, p.height)
    }

    protected val shader: PShader =
        vertPath
            ?.let { p.loadShader(fragPath, it) }
            ?: p.loadShader(fragPath)

    fun setDefaultShaderParams() {
        shader.set("u_resolution", resolution.x.toFloat(), resolution.y.toFloat())

        (mouse ?: Point(p.mouseX, p.mouseY))
            .apply { shader.set("u_mouse", x.toFloat(), y.toFloat()) }

        shader.set("u_time", p.millis() / 1000.0f)

        Triple(((color?.red ?: 0) / 255f), ((color?.green ?: 0) / 255f), ((color?.blue ?: 0) / 255f))
            .apply { shader.set("u_color", first, second, third) }
            .apply { println(this) }
    }

    fun set(param: String, vararg value: Any): ShaderWrapper {
        val value0 = value[0]
        if (value.size == 1) {
            when (value0) {
                is Float -> shader.set(param, value0)
                is Double -> shader.set(param, value0.toFloat())
                is Int -> shader.set(param, value0)
                is Long -> shader.set(param, value0.toInt())
                is Boolean -> shader.set(param, value0)
                is PVector -> shader.set(param, value0)
                is PMatrix2D -> shader.set(param, value0)
                is PMatrix3D -> shader.set(param, value0)
                is PImage -> shader.set(param, value0)
                else -> println("Cannot handle type ${value0::class.java.simpleName} : $param to $value")
            }
        } else when (value0) {
            is Float -> shader.set(
                param,
                FloatArray(value.size).apply { (value).forEachIndexed { i, v -> set(i, v as Float) } })
            is Int -> shader.set(
                param,
                IntArray(value.size).apply { (value).forEachIndexed { i, v -> set(i, v as Int) } })
            is Boolean -> shader.set(
                param,
                BooleanArray(value.size).apply { (value).forEachIndexed { i, v -> set(i, v as Boolean) } })
        }

        return this
    }

    open fun engage() {
        type?.let { p.shader(shader, it) } ?: p.shader(shader)
        //p.rect(0f, 0f, p.width.toFloat(), p.height.toFloat())
        p.pushMatrix {
            p.scale((p.width / resolution.x).toFloat(), (p.height / resolution.y).toFloat())
            p.rect(0f, 0f, resolution.x.toFloat(), resolution.y.toFloat())
        }
    }
}