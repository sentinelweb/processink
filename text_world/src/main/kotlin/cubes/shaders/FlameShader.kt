package cubes.shaders

import cubes.CubesProcessingView
import processing.core.PApplet

class FlameShader constructor(
    p: PApplet
) : ShaderWrapper(
    p,
    "${CubesProcessingView.BASE_RESOURCES}/cubes/st_coldFlameFrag.glsl"
)