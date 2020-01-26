package cubes.shaders

import cubes.CubesProcessingView.Companion.BASE_RESOURCES
import processing.core.PApplet
import processing.core.PConstants

class LineShader constructor(
    p: PApplet
) : ShaderWrapper(
    p,
    "$BASE_RESOURCES/cubes/linefrag.glsl",
    "$BASE_RESOURCES/cubes/linevert.glsl"
) {
    override val type: Int = PConstants.LINES

    init {
        setWeight(DEFAULT_WEIGHT)
    }

    fun setWeight(weight: Float) {
        set(PARAM_WEIGHT, weight)
    }

    companion object {
        const val DEFAULT_WEIGHT = 2f
        const val PARAM_WEIGHT = "weight"
    }
}