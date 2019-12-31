package cubes

data class CubesState constructor(
    var textList: TextList,
    var cubeList: CubeList,
    var rotationSpeed: Float,
    var animationTime: Float = 0f,
    var cubeRotationAxes: Triple<Boolean, Boolean, Boolean> = Triple(true, true, true),
    var info: PAppletInfo,
    var cubeScale: Float,
    var cubeScaleDist: Float
)

data class PAppletInfo(
    val width: Int,
    val height: Int
)