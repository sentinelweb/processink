package speecher.editor.subedit

import speecher.domain.Subtitles

data class SubEditState constructor(
    var readSub: Subtitles.Subtitle? = null,
    val readWordList: MutableList<String> = mutableListOf(),
    var writeSubs: MutableList<Subtitles.Subtitle> = mutableListOf(),
    var readWordSelected: Int? = null
)
