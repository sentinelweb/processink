package cubes.shaders

import processing.core.*
import processing.opengl.PShader

abstract class ShaderWrapper(
    protected val p: PApplet,
    fragPath: String,
    vertPath: String? = null
) {
    abstract protected val type: Int // POINTS, LINES, or TRIANGLES

    protected val shader: PShader =
        vertPath
            ?.let { p.loadShader(fragPath, it) }
            ?: p.loadShader(fragPath)

    private fun setDefaultShaderParams() {
        shader.set("u_resolution", p.width.toFloat(), p.height.toFloat())
        shader.set("u_mouse", p.mouseX.toFloat(), p.mouseY.toFloat())
        shader.set("u_time", p.millis() / 1000.0f)
    }

    fun set(param: String, value: Any):ShaderWrapper {
        when (value) {
            is Float -> shader.set(param, value)
            is Double -> shader.set(param, value.toFloat())
            is Int -> shader.set(param, value)
            is Long -> shader.set(param, value.toInt())
            is Boolean -> shader.set(param, value)
            is PVector -> shader.set(param, value)
            is PMatrix2D -> shader.set(param, value)
            is PMatrix3D -> shader.set(param, value)
            is PImage -> shader.set(param, value)
            else -> println("Cannot handle type ${value::class.java.simpleName} : $param to $value")
        }
        return this
    }

    fun engage() {
        p.shader(shader, type)
    }
}