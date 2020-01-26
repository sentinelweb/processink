package cubes

import cubes.objects.CubeList
import cubes.objects.TextList
import java.awt.Color

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
    var fillEndColor: Color = Color.ORANGE,
    var fillColor: Color = Color.YELLOW,
    var fillAlpha: Float = 255f,
    var textTransition: TextTransition = TextTransition.FADE,
    var textOrder: TextList.Ordering = TextList.Ordering.INORDER,
    var backgroundColor: Color = Color.BLACK
)

data class PAppletInfo(
    val width: Int,
    val height: Int
)

enum class TextTransition {
    FADE, FADE_ZOOM, ZOOM
}