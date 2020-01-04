package cubes.shaders

import cubes.CubesProcessingView
import processing.core.PApplet
import processing.core.PConstants

class NebulaShader constructor(
    p: PApplet,
    override val type: Int = PConstants.TRIANGLES
) : ShaderWrapper(
    p,
    "${CubesProcessingView.BASE_RESOURCES}/cubes/nebula.glsl"
)