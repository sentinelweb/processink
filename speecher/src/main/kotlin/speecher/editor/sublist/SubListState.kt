package speecher.editor.sublist

import speecher.domain.Subtitles

data class SubListState constructor(
    var subtitles: Subtitles? = null,
    var subtitlesDisplay: Map<Int, Subtitles.Subtitle> = mapOf(),
    var selectedIndex: Int? = null,
    var searchText: String? = null
)