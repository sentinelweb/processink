package cubes.shaders

import cubes.CubesProcessingView
import processing.core.PApplet

class NebulaShader constructor(
    p: PApplet
) : ShaderWrapper(
    p,
    "${CubesProcessingView.BASE_RESOURCES}/cubes/nebula.glsl"
)