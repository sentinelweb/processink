package cubes.motion

import cubes.models.Shape
import cubes.motion.interpolator.Interpolator
import processing.core.PVector
import provider.TimeProvider

abstract class TranslationMotion<out T : Shape> constructor(
    private val timeMs: Float,
    protected val target: List<PVector>,
    private val timeProvider: TimeProvider = TimeProvider(),
    private val interp: Interpolator? = null,
    endFunction: () -> Unit = {}
) : Motion<T, PVector>(timeProvider, interp, endFunction) {

    override fun <T : Shape> updateState(i: Int, shape: T) {
        if (!isStarted() || isEnded()) return
        val currentTime = timeProvider.getTime()
        val ratio = (currentTime - startTime) / timeMs

        val interpolated = interpolator?.getInterpolation(ratio) ?: ratio
        shape.position.set(
            interpolate(start[i].x, target[i].x, interpolated),
            interpolate(start[i].y, target[i].y, interpolated),
            interpolate(start[i].z, target[i].z, interpolated)
        )
    }

    override fun isEnded(): Boolean = isStarted() && (timeProvider.getTime() - startTime >= timeMs)

}
