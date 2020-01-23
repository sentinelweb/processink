package cubes.motion

import cubes.objects.Shape
import processing.core.PVector
import provider.TimeProvider

abstract class RotationMotion<out T : Shape> constructor(
    protected val timeMs: Float,
    protected val target: List<PVector>,
    private val timeProvider: TimeProvider = TimeProvider(),
    endFunction: () -> Unit = {}
) : Motion<T, PVector>(timeProvider, endFunction) {

    override fun <T : Shape> updateState(i: Int, shape: T) {
        if (!isStarted() || isEnded()) return
        val currentTime = timeProvider.getTime()
        val ratio = (currentTime - startTime) / timeMs

        shape.angle.set(
            interpolate(start[i].x, target[i].x, ratio),
            interpolate(start[i].y, target[i].y, ratio),
            interpolate(start[i].z, target[i].z, ratio)
        )
    }

    override fun isEnded(): Boolean = isStarted() && (timeProvider.getTime() - startTime >= timeMs)
}
