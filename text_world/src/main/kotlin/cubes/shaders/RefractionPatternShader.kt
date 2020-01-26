package cubes.shaders

import cubes.CubesProcessingView.Companion.BASE_RESOURCES
import processing.core.PApplet

class RefractionPatternShader constructor(
    p: PApplet
) : ShaderWrapper(
    p,
    "$BASE_RESOURCES/cubes/st_refractionPatternFrag.glsl"
) {

    fun setResolution(p: PApplet) {
        set(P_RESOLUTION, arrayOf(p.width.toFloat(), p.height.toFloat()))
    }

    fun setTime(time: Int) {
        set(P_TIME, time / 250f)
    }

    companion object {
        const val P_TIME = "time"
        const val P_RESOLUTION = "resolution"
    }
}