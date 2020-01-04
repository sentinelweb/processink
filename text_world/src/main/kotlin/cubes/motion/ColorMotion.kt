package cubes.motion

import cubes.objects.Shape
import provider.TimeProvider
import java.awt.Color

abstract class ColorMotion<out T : Shape> constructor(
    val timeMs: Float,
    val target: List<Color>,
    val timeProvider: TimeProvider = TimeProvider(),
    endFunction: () -> Unit = {}
) : Motion<T>(endFunction) {

    private val start by lazy {
        getStartData()
    }

    private val startTime = timeProvider.getTime()

    abstract fun getStartData(): List<Color>

    override fun <T : Shape> updateState(i: Int, shape: T) {
        if (isEnded()) return
        val currentTime = timeProvider.getTime()
        val ratio = (currentTime - startTime) / timeMs

        shape.fillColor = Color(
            interpolate(start[i].red.toFloat(), target[i].red.toFloat(), ratio).toInt(),
            interpolate(start[i].green.toFloat(), target[i].green.toFloat(), ratio).toInt(),
            interpolate(start[i].blue.toFloat(), target[i].blue.toFloat(), ratio).toInt(),
            interpolate(start[i].alpha.toFloat(), target[i].alpha.toFloat(), ratio).toInt()
        )
    }

    override fun isEnded() = timeProvider.getTime() - startTime > timeMs
}