package cubes

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
    var fillEndColor: Color,
    var fillColor: Color
)

data class PAppletInfo(
    val width: Int,
    val height: Int
)