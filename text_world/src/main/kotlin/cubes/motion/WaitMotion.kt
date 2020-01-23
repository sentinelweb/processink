package cubes.motion

import cubes.objects.Shape
import provider.TimeProvider

class WaitMotion constructor(
    val timeMs: Float,
    private val timeProvider: TimeProvider = TimeProvider(),
    endFunction: () -> Unit = {}
) : Motion<Shape, Any>(timeProvider, endFunction) {

    override fun isEnded() = isStarted() && (timeProvider.getTime() - startTime >= timeMs)

    override fun ensureEndState() {}

    override fun <T : Shape> updateState(i: Int, shape: T) {}

    override fun getStartData(): List<Any> = listOf()
}