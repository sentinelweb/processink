package cubes.motion

abstract class Motion constructor(
    private val endFunction: () -> Unit = {}
) {
    private var wasEndFunctionCalled = false

    abstract fun isEnded(): Boolean

    abstract fun ensureEndState()

    fun callEndOnce() {
        if (isEnded() && !wasEndFunctionCalled) {
            ensureEndState()
            endFunction()
            wasEndFunctionCalled = true
        }
    }
}