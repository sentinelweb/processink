package cubes.motion

import cubes.models.Shape
import provider.TimeProvider

class SeriesMotion<T : Shape> constructor(
    private val motions: List<Motion<T, Any>>, // todo make some object class/interface to limit types - separate model from render code first
    timeProvider: TimeProvider = TimeProvider(),
    endFunction: () -> Unit = {}
) : Motion<T, Any>(timeProvider, endFunction = endFunction) {

    private var currentIndex = 0

    override fun start() {
        motions[currentIndex].start()
        super.start()
    }

    override fun getStartData(): List<Any> = listOf()

    override fun isEnded() = motions.all { it.isEnded() }

    override fun ensureEndState() = motions.last().callEndOnce()

    fun next() {
        currentIndex++
        if (currentIndex < motions.size) {
            motions[currentIndex].start()
        } else {
            callEndOnce()
        }
    }

    override fun <T : Shape> updateState(i: Int, shape: T) {
        if (!motions[currentIndex].isEnded()) {
            motions[currentIndex].updateState(i, shape)
        } else {
            motions[currentIndex].callEndOnce()
            next()
        }
    }

}