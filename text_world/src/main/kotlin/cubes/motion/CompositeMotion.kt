package cubes.motion

import cubes.objects.Shape

class CompositeMotion<T:Shape> constructor(
    private val motions: List<Motion<T>>, // todo make some object class/interface to limit types - separate model from render code first
    endFunction: () -> Unit = {}
) : Motion<T>(endFunction) {

    override fun isEnded() = motions.all { it.isEnded() }

    override fun ensureEndState() = motions.forEach { it.ensureEndState() }

    override fun <T:Shape> updateState(i: Int, shape: T) = motions.forEach { it.updateState(i, shape) }

}