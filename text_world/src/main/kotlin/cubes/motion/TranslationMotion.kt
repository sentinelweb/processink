package cubes.motion

import cubes.objects.Shape
import processing.core.PVector
import provider.TimeProvider

abstract class TranslationMotion<out T : Shape> constructor(
    val timeMs: Float,
    val target: List<PVector>,
    val timeProvider: TimeProvider = TimeProvider(),
    endFunction: () -> Unit = {}
) : Motion<T>(endFunction) {

    private val start by lazy {
        getStartData()
    }
    private val startTime = timeProvider.getTime()

    abstract fun getStartData(): List<PVector>

    override fun <T : Shape> updateState(i: Int, shape: T) {
        if (isEnded()) return
        val currentTime = timeProvider.getTime()
        val ratio = (currentTime - startTime) / timeMs

        shape.position.set(
            interpolate(start[i].x, target[i].x, ratio),
            interpolate(start[i].y, target[i].y, ratio),
            interpolate(start[i].z, target[i].z, ratio)
        )
    }

    override fun isEnded() = timeProvider.getTime() - startTime > timeMs


}
