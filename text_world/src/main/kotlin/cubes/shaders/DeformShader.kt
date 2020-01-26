package cubes.shaders

import cubes.CubesProcessingView.Companion.BASE_RESOURCES
import processing.core.PApplet
import processing.core.PConstants.REPEAT
import processing.core.PImage

class DeformShader constructor(
    p: PApplet
) : ShaderWrapper(
    p,
    "$BASE_RESOURCES/cubes/deform.glsl"
) {

    override val type: Int? = null

    var texName: String = "tex1.jpg"
        set(value) {
            field = value
            tex = p.loadImage("$BASE_RESOURCES/cubes/$value")
        }

    private var tex: PImage = p.loadImage("$BASE_RESOURCES/cubes/$texName")

    override fun engage() {
        super.engage()
        p.textureWrap(REPEAT)
        p.image(tex, 0f, 0f, p.width.toFloat(), p.height.toFloat())
    }
}