package cubes.shaders

import cubes.CubesProcessingView.Companion.BASE_RESOURCES
import processing.core.PApplet

class ProteanCloudsShader constructor(
    p: PApplet
) : ShaderWrapper(
    p,
    "$BASE_RESOURCES/cubes/st_protean_clouds.glsl"
)