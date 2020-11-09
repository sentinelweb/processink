package speecher.editor.sublist

import speecher.domain.Subtitles

data class SubListState constructor(
    var subtitles: Subtitles? = null,
    var selectedIndex: Int? = null
)