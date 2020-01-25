package cubes.motion

import cubes.objects.Shape
import provider.TimeProvider

class TestMotion constructor(
    val timeMs: Float,
    val timeProvider: TimeProvider,
    end: () -> Unit
) : Motion<Shape, Any>(timeProvider, endFunction = end) {
    override fun getStartData(): List<Any> = listOf()

    override fun isEnded(): Boolean = isStarted() && (timeProvider.getTime() - startTime >= timeMs)


    override fun ensureEndState() {

    }

    override fun <T : Shape> updateState(i: Int, shape: T) {

    }

}