package cubes

data class CubesState constructor(
    var textList: TextList,
    var cubeList: CubeList,
    var rotationSpeed: Float,
    var cubeAlignTime: Float = 0f,
    var cubeRotationAxes:Triple<Boolean,Boolean,Boolean> = Triple(true, true, true)
)