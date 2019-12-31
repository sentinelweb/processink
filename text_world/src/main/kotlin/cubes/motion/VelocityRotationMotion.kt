package cubes.motion

import cubes.Cube
import cubes.CubesState

class VelocityRotationMotion(
    private val rotationSpeed: Float,
    private val rotationOffset: Float,
    private val motionRotation: Triple<Boolean, Boolean, Boolean> = Triple(true, true, true),
    val endFunction: () -> Unit = {}
) : Motion<Cube>() {

    override fun isEnded() = false

    override fun ensureEndState() = Unit

    override fun updateState(i: Int, shape: Cube) {
        val increment = rotationSpeed + rotationOffset * (i + 1)
        shape.angle = Triple(
            shape.angle.first + if (motionRotation.first) increment else 0f,
            shape.angle.second + if (motionRotation.second) increment else 0f,
            shape.angle.third + if (motionRotation.third) increment else 0f
        )
    }

    companion object {
        fun make(state:CubesState) = VelocityRotationMotion(state.rotationSpeed, state.rotationOffset, state.cubeRotationAxes)
    }
}