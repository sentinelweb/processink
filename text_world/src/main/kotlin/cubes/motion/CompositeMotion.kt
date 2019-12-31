package cubes.motion

class CompositeMotion<T> constructor(
    private val motions: List<Motion<T>>, // todo make some object class/interface to limit types - separate model from render code first
    endFunction: () -> Unit = {}
) : Motion<T>(endFunction) {

    override fun isEnded() = motions.all { it.isEnded() }

    override fun ensureEndState() = motions.forEach { it.ensureEndState() }

    override fun updateState(i: Int, shape: T) = motions.forEach { it.updateState(i, shape) }

}