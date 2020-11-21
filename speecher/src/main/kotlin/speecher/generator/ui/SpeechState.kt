package speecher.generator.ui

import speecher.domain.Sentence
import speecher.domain.Subtitles

data class SpeechState(
    val wordList: List<Sentence.Item> = listOf(),
    var subs: List<Subtitles.Subtitle>? = null
)