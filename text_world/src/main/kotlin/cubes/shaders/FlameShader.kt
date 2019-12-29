package cubes.shaders

import cubes.Cubes
import processing.core.PApplet
import processing.core.PConstants

// doesnt work
class FlameShader constructor(
    p:PApplet,
    override val type: Int = PConstants.POINT
): ShaderWrapper(p,
    "${Cubes.BASE_RESOURCES}/shadertoy/st_coldFlameFrag.glsl")