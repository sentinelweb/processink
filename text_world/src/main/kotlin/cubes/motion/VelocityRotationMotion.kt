package cubes.motion

import cubes.CubesState
import cubes.objects.Cube
import cubes.objects.Shape
import processing.core.PVector
import provider.TimeProvider

class VelocityRotationMotion(
    private val rotationSpeed: Float,
    private val rotationOffset: Float,
    private val motionRotation: Triple<Boolean, Boolean, Boolean> = Triple(true, true, true),
    timeProvider: TimeProvider = TimeProvider(),
    endFunction: () -> Unit = {}
) : RotationMotion<Cube>(0f, listOf(), timeProvider, null, endFunction) {

    override fun isEnded() = false

    override fun ensureEndState() = Unit

    override fun <T : Shape> updateState(i: Int, shape: T) {
        val increment = rotationSpeed + rotationOffset * (i + 1)
        shape.angle.set(
            shape.angle.x + if (motionRotation.first) increment else 0f,
            shape.angle.y + if (motionRotation.second) increment else 0f,
            shape.angle.z + if (motionRotation.third) increment else 0f
        )
    }

    companion object {
        fun makeCubesRotation(state: CubesState) =
            VelocityRotationMotion(state.cubesRotationSpeed, state.cubesRotationOffset, state.cubeRotationAxes)
    }

    override fun getStartData(): List<PVector> = listOf()
}