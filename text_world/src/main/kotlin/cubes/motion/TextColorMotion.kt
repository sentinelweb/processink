package cubes.motion

import cubes.objects.TextList
import provider.TimeProvider
import java.awt.Color

class TextColorMotion constructor(
    private val textList: TextList,
    timeMs: Float,
    val start: Color? = null,
    target: Color = Color.WHITE,
    timeProvider: TimeProvider = TimeProvider(),
    endFunction: () -> Unit = {}
) : ColorMotion<TextList.Text>(timeMs, textList.texts.map { target }, timeProvider, endFunction) {

    override fun ensureEndState() {
        textList.texts.forEachIndexed { i, cube ->
            cube.fillColor = target[i] // parent target
        }
    }

    override fun getStartData(): List<Color> =
        textList.texts.map { text -> start ?: text.fillColor }

}