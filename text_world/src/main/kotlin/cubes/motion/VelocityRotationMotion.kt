package cubes.motion

import cubes.Cube
import cubes.CubeList

class VelocityRotationMotion(
    private val rotationSpeed: Float,
    private val motionRotation: Triple<Boolean, Boolean, Boolean> = Triple(true, true, true),
    val endFunction: () -> Unit = {}
) : CubeList.MotionUpdater() {

    override fun isEnded() = false
    override fun ensureEndState() = Unit

    override fun updateState(i: Int, cube: Cube) {
        val increment = rotationSpeed * (i + 1)
        cube.angle = Triple(
            cube.angle.first + if (motionRotation.first) increment else 0f,
            cube.angle.second + if (motionRotation.second) increment else 0f,
            cube.angle.third + if (motionRotation.third) increment else 0f
        )
    }
}