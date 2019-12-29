package cubes.shaders

import cubes.Cubes
import processing.core.PApplet
import processing.core.PConstants

class LineShader constructor(
    p:PApplet,
    override val type: Int = PConstants.LINES
): ShaderWrapper(p,
    "${Cubes.BASE_RESOURCES}/cubes/linefrag.glsl",
    "${Cubes.BASE_RESOURCES}/cubes/linevert.glsl") {
    fun x() {}
}