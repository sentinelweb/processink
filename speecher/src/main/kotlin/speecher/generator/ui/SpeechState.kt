package speecher.generator.ui

import speecher.domain.Sentence
import speecher.domain.Subtitles

data class SpeechState(
    var wordList: List<Sentence.Word> = listOf(),
    var subs: List<Subtitles.Subtitle>? = null,
    var subsDisplay: List<Subtitles.Subtitle>? = null,
    var cursorPos: Int = 0,
    var searchText: String? = null,
    var sortOrder: SpeechContract.SortOrder = SpeechContract.SortOrder.NATURAL
)