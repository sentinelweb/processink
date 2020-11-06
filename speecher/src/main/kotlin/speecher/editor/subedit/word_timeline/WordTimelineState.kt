package speecher.editor.subedit.word_timeline

import speecher.domain.Subtitles

data class WordTimelineState constructor(
    val subs: MutableList<Subtitles.Subtitle> = mutableListOf(),
    val limits: FloatArray = floatArrayOf(0f, 0f),
    var currentWord: String? = null,
    val currentWordLimits: FloatArray = floatArrayOf(0f, 0f)
)