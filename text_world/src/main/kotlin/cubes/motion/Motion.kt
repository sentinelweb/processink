package cubes.motion

import cubes.objects.Shape

abstract class Motion<out T : Shape> constructor(
    private val endFunction: () -> Unit = {}
) {
    private var wasEndFunctionCalled = false

    abstract fun isEnded(): Boolean

    abstract fun ensureEndState()

    abstract fun <T : Shape> updateState(i: Int, shape: T)

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