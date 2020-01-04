package cubes.shaders

import cubes.CubesProcessingView
import processing.core.PApplet
import processing.core.PConstants

class FlameShader constructor(
    p:PApplet,
    override val type: Int = PConstants.POINTS
): ShaderWrapper(p,
    "${CubesProcessingView.BASE_RESOURCES}/shadertoy/st_coldFlameFrag.glsl")