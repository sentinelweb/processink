package cubes.motion

import cubes.objects.Cube
import cubes.objects.CubeList
import processing.core.PVector
import provider.TimeProvider

class CubeRotationAlignMotion constructor(
    private val cubeList: CubeList,
    timeMs: Float,
    target: List<PVector> = cubeList.cubes.map { PVector() },
    timeProvider: TimeProvider = TimeProvider(),
    endFunction: () -> Unit = {}
) : RotationMotion<Cube>(timeMs, target, timeProvider, endFunction) {

    override fun ensureEndState() {
        cubeList.cubes.forEachIndexed { i, cube ->
            cube.angle = target[i]
        }
    }

    override fun getStartData(): List<PVector> =
        cubeList.cubes.map { cube -> wrapTo2Pi(cube.angle.copy()) }

    fun wrapTo2Pi(angle: Float): Float {
        val minusPiToPi = Math.atan2(Math.sin(angle.toDouble()), Math.cos(angle.toDouble())).toFloat()
        return if (minusPiToPi < 0) (2 * Math.PI + minusPiToPi).toFloat() else minusPiToPi
    }

    fun wrapTo2Pi(angles: PVector) =
        PVector(wrapTo2Pi(angles.x), wrapTo2Pi(angles.y), wrapTo2Pi(angles.z))

}