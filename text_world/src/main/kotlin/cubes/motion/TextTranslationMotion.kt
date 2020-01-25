package cubes.motion


import cubes.motion.interpolator.Interpolator
import cubes.objects.TextList
import processing.core.PVector
import provider.TimeProvider

class TextTranslationMotion constructor(
    private val textList: TextList,
    timeMs: Float,
    target: PVector = PVector(),
    private val startPosition: PVector = PVector(),
    timeProvider: TimeProvider = TimeProvider(),
    interp: Interpolator? = null,
    endFunction: () -> Unit = {}
) : TranslationMotion<TextList.Text>(timeMs, textList.texts.map { target }, timeProvider, interp, endFunction) {

    override fun getStartData() = textList.texts.map { startPosition }

    override fun ensureEndState() {
        textList.texts.forEachIndexed { i, text ->
            text.position.set(target[i])
        }
    }
}