package cubes.motion


import cubes.objects.TextList
import processing.core.PVector
import provider.TimeProvider

class TextTranslationMotion constructor(
    private val textList: TextList,
    timeMs: Float,
    target: List<PVector> = textList.texts.map { PVector() },
    timeProvider: TimeProvider = TimeProvider(),
    endFunction: () -> Unit = {}
) : TranslationMotion<TextList.Text>(timeMs, target, timeProvider, endFunction) {

    override fun getStartData() = textList.texts.map { text -> text.position.copy() }

    override fun ensureEndState() {
        textList.texts.forEachIndexed { i, text ->
            text.scale.set(target[i])
        }
    }

    companion object {

        fun grid(textList: TextList, timeMs: Float, dimension: Int, spacing: Float, endFunction: () -> Unit = {}) =
            TextTranslationMotion(
                textList,
                timeMs,
                textList.texts.mapIndexed { i, cube ->
                    val row = i % dimension
                    val col = i / dimension
                    PVector(
                        -spacing / 2 + (row - 1) * spacing,//TODO assuming 4 dimension sub len/2 somehow
                        -spacing / 2 + (col - 1) * spacing,
                        0f
                    )
                },
                endFunction = endFunction
            )

        fun line(textList: TextList, timeMs: Float, spacing: Float, endFunction: () -> Unit = {}) =
            TextTranslationMotion(
                textList,
                timeMs,
                textList.texts.mapIndexed { i, cube ->
                    PVector(
                        -spacing / 2 + (i) * spacing / textList.texts.size,
                        0f,
                        0f
                    )
                },
                endFunction = endFunction
            )

    }
}