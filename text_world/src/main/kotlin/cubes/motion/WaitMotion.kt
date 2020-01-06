package cubes.motion

import cubes.objects.Shape
import provider.TimeProvider

class WaitMotion constructor(
    val timeMs: Float,
    val timeProvider: TimeProvider = TimeProvider(),
    endFunction: () -> Unit = {}
) : Motion<Shape>(endFunction) {

    private val startTime = timeProvider.getTime()

    override fun isEnded() = timeProvider.getTime() - startTime > timeMs

    override fun ensureEndState() {}

    override fun <T : Shape> updateState(i: Int, shape: T) {}
}