package cubes.shaders

import cubes.CubesProcessingView
import processing.core.PApplet
import processing.core.PConstants

class LineShader constructor(
    p: PApplet
): ShaderWrapper(p,
    "${CubesProcessingView.BASE_RESOURCES}/cubes/linefrag.glsl",
    "${CubesProcessingView.BASE_RESOURCES}/cubes/linevert.glsl") {
    override val type: Int = PConstants.LINES

    init {
        setWeight(DEFAULT_WEIGHT)
    }

    fun setWeight(weight:Float) {
        set(PARAM_WEIGHT, weight)
    }

    companion object {
        const val DEFAULT_WEIGHT = 2f
        const val PARAM_WEIGHT = "weight"
    }
}