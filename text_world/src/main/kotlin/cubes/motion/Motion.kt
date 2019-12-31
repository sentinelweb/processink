package cubes.motion

abstract class Motion<T> constructor(
    private val endFunction: () -> Unit = {}
) {
    private var wasEndFunctionCalled = false

    abstract fun isEnded(): Boolean

    abstract fun ensureEndState()

    abstract fun updateState(i:Int, shape:T)

    fun callEndOnce() {
        if (isEnded() && !wasEndFunctionCalled) {
            ensureEndState()
            endFunction()
            wasEndFunctionCalled = true
        }
    }

    companion object {
        fun interpolate(startPos: Float, endPos: Float, ratio: Float) =
            startPos + (endPos - startPos) * ratio
    }
}