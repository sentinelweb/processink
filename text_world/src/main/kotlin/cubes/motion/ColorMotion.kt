package cubes.motion

import cubes.objects.Shape
import provider.TimeProvider
import java.awt.Color

abstract class ColorMotion<out T : Shape> constructor(
    protected val timeMs: Float,
    protected val target: List<Color>,
    private val timeProvider: TimeProvider = TimeProvider(),
    endFunction: () -> Unit = {}
) : Motion<T, Color>(timeProvider, endFunction = endFunction) {

    override fun <T : Shape> updateState(i: Int, shape: T) {
        if (!isStarted() || isEnded()) return
        val currentTime = timeProvider.getTime()
        val ratio = (currentTime - startTime) / timeMs

        shape.fillColor = Color(
            interpolate(start[i].red.toFloat(), target[i].red.toFloat(), ratio).toInt(),
            interpolate(start[i].green.toFloat(), target[i].green.toFloat(), ratio).toInt(),
            interpolate(start[i].blue.toFloat(), target[i].blue.toFloat(), ratio).toInt(),
            interpolate(start[i].alpha.toFloat(), target[i].alpha.toFloat(), ratio).toInt()
        )
    }

    override fun isEnded(): Boolean = isStarted() && (timeProvider.getTime() - startTime >= timeMs)

}