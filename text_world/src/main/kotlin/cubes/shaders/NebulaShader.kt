package cubes.shaders

import cubes.CubesProcessingView.Companion.BASE_RESOURCES
import processing.core.PApplet

class NebulaShader constructor(
    p: PApplet
) : ShaderWrapper(
    p,
    "$BASE_RESOURCES/cubes/nebula.glsl"
)