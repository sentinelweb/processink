package cubes.shaders

import cubes.CubesProcessingView.Companion.BASE_RESOURCES
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PImage

class CloudsShader constructor(
    p: PApplet
) : ShaderWrapper(
    p,
    "$BASE_RESOURCES/cubes/st_clouds.glsl"
) {
    var texName: String = "st_clouds_noise.png"
        set(value) {
            field = value
            tex = p.loadImage("$BASE_RESOURCES/cubes/$value")
        }

    private var tex: PImage = p.loadImage("$BASE_RESOURCES/cubes/$texName")

    override fun engage() {
        super.engage()
        p.textureWrap(PConstants.REPEAT)
        p.image(tex, 0f, 0f, p.width.toFloat(), p.height.toFloat())
    }
}