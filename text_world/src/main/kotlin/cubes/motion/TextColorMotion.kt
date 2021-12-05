package cubes.motion

import cubes.models.TextList
import provider.TimeProvider
import java.awt.Color

class TextColorMotion constructor(
    private val textList: TextList,
    timeMs: Float,
    val startColor: Color? = null,
    target: Color = Color.WHITE,
    timeProvider: TimeProvider = TimeProvider(),
    endFunction: () -> Unit = {}
) : ColorMotion<TextList.Text>(timeMs, textList.texts.map { target }, timeProvider, endFunction) {

    init {
        textList.texts.forEach { it.fill = true }
    }

    override fun ensureEndState() {
        textList.texts.forEachIndexed { i, text ->
            text.fillColor = target[i] // parent target
        }
    }

    override fun getStartData(): List<Color> =
        textList.texts.map { text -> startColor ?: text.fillColor }
}