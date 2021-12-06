package cubes

import cubes.CubesContract.BackgroundShaderType.REFRACTION_PATTERN
import cubes.CubesContract.TextTransition.FADE
import cubes.models.CubeList
import cubes.models.Shape
import cubes.models.TextList
import cubes.models.TextList.Ordering.INORDER
import cubes.particles.ParticleSystem
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import processing.core.PApplet
import processing.core.PVector
import java.awt.Color
import java.awt.Font

@Serializable
data class CubesState constructor(
    var textList: TextList,
    var cubeList: CubeList,
    var cubesRotationSpeed: Float,
    var animationTime: Float = 2000f,
    var cubeRotationAxes: Triple<Boolean, Boolean, Boolean> = Triple(true, true, true),
    var info: PAppletInfo,
    var cubeScale: Float,
    var cubeScaleDist: Float,
    var cubesRotationOffset: Float,
    @Contextual
    var cubesFillEndColor: Color = Color.ORANGE,
    @Contextual
    var cubesFillStartColor: Color = Color.YELLOW,
    var cubesFillAlpha: Float = 255f,
    @Contextual
    var cubesStrokeColor: Color = Color.RED,
    // text
    var textTransition: CubesContract.TextTransition = FADE,
    var textOrder: TextList.Ordering = INORDER,
    @Contextual
    var textColor: Color = Color.ORANGE,
    @Contextual
    var textFont: Font? = null,
    @Contextual
    var backgroundColor: Color = Color.BLACK,
    var background: CubesContract.BackgroundShaderType = REFRACTION_PATTERN,
    @Transient
    val models: MutableList<Shape> = mutableListOf(),
    // psys
    @Transient
    val particleSystems: MutableList<ParticleSystem> = mutableListOf(),
    @Contextual
    var particleFillColor: Color? = null,
    @Contextual
    var particleStrokeColor: Color? = null,
    var particleShape: CubesContract.ParticleShape = CubesContract.ParticleShape.CIRCLE,
    var particleShapePath: String? = null, // for SVG /  image
    var particleNum: Int = 50,
    var particleSize: Float = 2f,
    @Contextual
    var particlePosition: PVector = PVector(0.5f, 0.5f, 0f),
    var particleLifespan: Int = 1000, //msec
) {
    companion object {
        fun makeFromState(p: PApplet) = CubesState(
            textList = TextList(p)
                .apply { fillColor = Color.YELLOW; visible = false },
            cubeList = CubeList(p, 16, 50f, 400f)
                .apply { visible = true },
            cubesRotationSpeed = 0.001f,
            cubeScale = 10f,
            cubeScaleDist = 0f,
            cubesRotationOffset = 0f,
            cubesFillStartColor = Color.WHITE,
            cubesFillEndColor = Color.GRAY,
            animationTime = 1000f,
            info = PAppletInfo(p.width, p.height)
        )
    }
}

@Serializable
data class PAppletInfo(
    val width: Int,
    val height: Int
)
