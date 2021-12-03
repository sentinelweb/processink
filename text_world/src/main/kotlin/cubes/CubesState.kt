package cubes

import cubes.CubesContract.BackgroundShaderType.REFRACTION_PATTERN
import cubes.CubesContract.TextTransition.FADE
import cubes.models.CubeList
import cubes.models.Shape
import cubes.models.TextList
import cubes.models.TextList.Ordering.INORDER
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.awt.Color

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
    var textTransition: CubesContract.TextTransition = FADE,
    var textOrder: TextList.Ordering = INORDER,
    @Contextual
    var backgroundColor: Color = Color.BLACK,
    var background: CubesContract.BackgroundShaderType = REFRACTION_PATTERN,
    @Transient
    val models: MutableList<Shape> = mutableListOf()
)

@Serializable
data class PAppletInfo(
    val width: Int,
    val height: Int
)
