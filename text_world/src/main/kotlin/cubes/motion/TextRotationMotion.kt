package cubes.motion


import cubes.models.TextList
import cubes.motion.interpolator.Interpolator
import processing.core.PVector
import provider.TimeProvider
import speecher.util.wrapper.LogWrapper

class TextRotationMotion constructor(
    private val textList: TextList,
    timeMs: Float,
    target: PVector = PVector(),
    private val startAngle: PVector = PVector(),
    timeProvider: TimeProvider = TimeProvider(),
    interp: Interpolator? = null,
    endFunction: () -> Unit = {},
    private val log: LogWrapper
) : RotationMotion<TextList.Text>(timeMs, textList.texts.map { target }, timeProvider, interp, endFunction) {

    override fun getStartData() = textList.texts.map { startAngle }

    override fun ensureEndState() {
        textList.texts.forEachIndexed { i, text ->
            text.angle.set(target[i])
        }
    }
}