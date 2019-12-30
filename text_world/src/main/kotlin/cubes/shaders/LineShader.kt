package cubes.shaders

import cubes.CubesProcessingView
import processing.core.PApplet
import processing.core.PConstants

class LineShader constructor(
    p:PApplet,
    override val type: Int = PConstants.LINES
): ShaderWrapper(p,
    "${CubesProcessingView.BASE_RESOURCES}/cubes/linefrag.glsl",
    "${CubesProcessingView.BASE_RESOURCES}/cubes/linevert.glsl") {
    init {
        setWeight(DEFAULT_WEIGHT)
    }
    fun setWeight(weight:Float) {
        set(PARAM_WEIGHT, weight)
    }

    companion object {
        const val DEFAULT_WEIGHT = 5f
        const val PARAM_WEIGHT = "weight"
    }
}