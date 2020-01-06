package cubes.shaders

import processing.core.*
import processing.opengl.PShader

abstract class ShaderWrapper(
    protected val p: PApplet,
    fragPath: String,
    vertPath: String? = null
) {
    open protected val type: Int = PConstants.TRIANGLES // POINTS, LINES, or TRIANGLES

    protected val shader: PShader =
        vertPath
            ?.let { p.loadShader(fragPath, it) }
            ?: p.loadShader(fragPath)

    fun setDefaultShaderParams() {
        shader.set("u_resolution", p.width.toFloat(), p.height.toFloat())
        shader.set("u_mouse", p.mouseX.toFloat(), p.mouseY.toFloat())
        shader.set("u_time", p.millis() / 1000.0f)
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

    fun engage() {
        p.shader(shader, type)
    }
}