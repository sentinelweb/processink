package cubes.motion

import cubes.models.Shape
import provider.TimeProvider

class WaitMotion<out T : Shape> constructor(
    val timeMs: Float,
    private val timeProvider: TimeProvider = TimeProvider(),
    endFunction: () -> Unit = {}
) : Motion<T, Any>(timeProvider, endFunction = endFunction) {

    override fun isEnded() = isStarted() && (timeProvider.getTime() - startTime >= timeMs)

    override fun ensureEndState() {}

    override fun <T : Shape> updateState(i: Int, shape: T) {}

    override fun getStartData(): List<Any> = listOf()
}