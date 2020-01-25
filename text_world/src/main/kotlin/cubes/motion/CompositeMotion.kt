package cubes.motion

import cubes.objects.Shape
import provider.TimeProvider

class CompositeMotion<T : Shape> constructor(
    private val motions: List<Motion<T, Any>>, // todo make some object class/interface to limit types - separate model from render code first
    timeProvider: TimeProvider = TimeProvider(),
    endFunction: () -> Unit = {}
) : Motion<T, Any>(timeProvider, endFunction = endFunction) {

    override fun start() {
        motions.forEach { it.start() }
    }

    override fun getStartData(): List<Any> = listOf()

    override fun isEnded() = motions.all {
        //println("${it} : ${it.isEnded()}")
        it.isEnded()
    }

    override fun ensureEndState() = motions.forEach {
        it.callEndOnce()
    }

    override fun <T : Shape> updateState(i: Int, shape: T) {
        if (!isEnded()) {
            motions.forEach { it.updateState(i, shape) }
        } else {
            callEndOnce()
        }
    }
}