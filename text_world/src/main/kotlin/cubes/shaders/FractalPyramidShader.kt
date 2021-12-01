package cubes.shaders

import cubes.CubesProcessingView.Companion.BASE_RESOURCES
import processing.core.PApplet

class FractalPyramidShader constructor(
    p: PApplet
) : ShaderWrapper(
    p,
    "$BASE_RESOURCES/cubes/st_fractal_pyramid.glsl"
)