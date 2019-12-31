package cubes.motion

import cubes.Cube
import cubes.CubeList
import provider.TimeProvider
import kotlin.math.abs

class CubeRotationAlignMotion constructor(
    private val cubeList: CubeList,
    private val timeMs: Float,
    private val endAngle: Triple<Float,Float,Float> = Triple(0f,0f,0f),
    private val timeProvider: TimeProvider = TimeProvider(),
    endFunction: () -> Unit = {}
) : Motion<Cube>(endFunction) {

    private val start = cubeList.cubes.map { cube -> wrapTo2Pi(cube.angle.copy()) }
    private val startTime = timeProvider.getTime()

    override fun updateState(i: Int, shape: Cube) {
        if (isEnded()) return
        val currentTime = timeProvider.getTime()
        val ratio = (currentTime - startTime) / timeMs

        shape.angle = Triple(
            interpolate(start[i].first, endAngle.first, ratio),
            interpolate(start[i].second, endAngle.second, ratio),
            interpolate(start[i].third, endAngle.third, ratio)
        )
    }

    override fun isEnded() = timeProvider.getTime() - startTime > timeMs

    override fun ensureEndState() {
        cubeList.cubes.forEach {
            it.angle = endAngle
        }
    }

    fun wrapTo2Pi(angle: Float): Float {
        val minusPiToPi = Math.atan2(Math.sin(angle.toDouble()), Math.cos(angle.toDouble())).toFloat()
        return if (minusPiToPi < 0) (2 * Math.PI + minusPiToPi).toFloat() else minusPiToPi
    }

    fun wrapTo2Pi(angles: Triple<Float, Float, Float>) =
        angles.copy(wrapTo2Pi(angles.first), wrapTo2Pi(angles.second), wrapTo2Pi(angles.third))
}