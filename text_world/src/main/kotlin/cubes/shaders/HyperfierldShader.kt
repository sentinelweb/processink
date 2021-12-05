package cubes.shaders

import cubes.CubesProcessingView.Companion.BASE_RESOURCES
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PImage
import java.awt.Point

class HyperfierldShader constructor(
    p: PApplet
) : ShaderWrapper(
    p,
    "$BASE_RESOURCES/cubes/st_hyperfield.glsl"
) {
    var texName: String = "st_hyperfield_noise.png"
        set(value) {
            field = value
            tex = p.loadImage("$BASE_RESOURCES/cubes/$value")
        }

    init {
        speed(1f)
    }

    fun speed(factor: Float) {
        mouse = Point((factor * p.width).toInt(), 0)
    }

    private var tex: PImage = p.loadImage("$BASE_RESOURCES/cubes/$texName")

    override fun engage() {
        super.engage()
        p.textureWrap(PConstants.REPEAT)
        p.image(tex, 0f, 0f, p.width.toFloat(), p.height.toFloat())
    }
}