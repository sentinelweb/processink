package cubes.shaders

import cubes.CubesProcessingView.Companion.BASE_RESOURCES
import processing.core.PApplet
import java.awt.Point

class WaterShader constructor(
    p: PApplet
) : ShaderWrapper(
    p,
    "$BASE_RESOURCES/cubes/st_oceanFrag.glsl"
) {
    init {
        thirdHorizon()
    }

    fun waterVortex() {
        mouse = Point(0, 0)
    }

    fun halfHorizon() {
        mouse = Point(p.width / 2, p.height / 2)
    }

    fun thirdHorizon() {
        mouse = Point(p.width / 2, p.height * 2 / 3)
    }
}