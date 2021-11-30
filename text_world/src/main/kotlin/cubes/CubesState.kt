package cubes

import cubes.CubesContract.BackgroundShaderType.REFRACTION_PATTERN
import cubes.objects.CubeList
import cubes.objects.TextList
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.awt.Color

@Serializable
data class CubesState constructor(
    var textList: TextList,
    var cubeList: CubeList,
    var rotationSpeed: Float,
    var animationTime: Float = 0f,
    var cubeRotationAxes: Triple<Boolean, Boolean, Boolean> = Triple(true, true, true),
    var info: PAppletInfo,
    var cubeScale: Float,
    var cubeScaleDist: Float,
    var rotationOffset: Float,
    @Contextual
    var fillEndColor: Color = Color.ORANGE,
    @Contextual
    var fillColor: Color = Color.YELLOW,
    var fillAlpha: Float = 255f,
    var textTransition: TextTransition = TextTransition.FADE,
    var textOrder: TextList.Ordering = TextList.Ordering.INORDER,
    @Contextual
    var backgroundColor: Color = Color.BLACK,
    var background: CubesContract.BackgroundShaderType = REFRACTION_PATTERN
)

@Serializable
data class PAppletInfo(
    val width: Int,
    val height: Int
)

enum class TextTransition {
    FADE, FADE_ZOOM, ZOOM
}