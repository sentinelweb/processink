package cubes.motion

import cubes.objects.Shape
import provider.TimeProvider

abstract class Motion<out T : Shape, out D : Any> constructor(
    private val timeProvider: TimeProvider,
    private val endFunction: () -> Unit = {}
) {
    protected var startTime = NOT_STARTED

    open fun start() {
        startTime = timeProvider.getTime()
    }

    protected val start by lazy {
        getStartData()
    }

    fun isStarted() = startTime != NOT_STARTED

    abstract fun getStartData(): List<D>

    private var wasEndFunctionCalled = false

    abstract fun isEnded(): Boolean

    abstract fun ensureEndState()

    fun <T : Shape> execute(i: Int, shape: T) {
        if (!isEnded()) {
            updateState(i, shape)
        } else {
            callEndOnce()
        }
    }

    abstract fun <T : Shape> updateState(i: Int, shape: T)

    fun callEndOnce() {
        if (isEnded() && !wasEndFunctionCalled) {
            ensureEndState()
            endFunction()
            wasEndFunctionCalled = true
        }
    }

    companion object {
        val NOT_STARTED = -1L

        fun interpolate(startPos: Float, endPos: Float, ratio: Float) =
            startPos + (endPos - startPos) * ratio
    }
}