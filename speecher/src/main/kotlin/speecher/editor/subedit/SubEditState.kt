package speecher.editor.subedit

import speecher.domain.Subtitles

data class SubEditState constructor(
    var readSub: Subtitles.Subtitle? = null,
    val readWordList: MutableList<String> = mutableListOf(),
    val writeSubs: MutableList<Subtitles.Subtitle> = mutableListOf(),
    var readWordSelected: Int = -1,
    var writeWordSelected: Int = -1,
    val readToWriteMap: MutableMap<Int, Int> = mutableMapOf(),
    val sliderPositions: FloatArray = floatArrayOf(0f, 0f), // slider pos 0 .. 1
    val sliderTimes: FloatArray = floatArrayOf(0f, 0f), // selected time range float secs
    val sliderLimits: FloatArray = floatArrayOf(0f, 0f) // slider limits range start and end
)
