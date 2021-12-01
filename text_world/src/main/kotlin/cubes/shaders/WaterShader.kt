package cubes.shaders

import cubes.CubesProcessingView.Companion.BASE_RESOURCES
import processing.core.PApplet
import java.awt.Point

class WaterShader constructor(
    p: PApplet
) : ShaderWrapper(
    p,
    "$BASE_RESOURCES/cubes/st_water.glsl",
    dimOverride = Point(320, 180)
)