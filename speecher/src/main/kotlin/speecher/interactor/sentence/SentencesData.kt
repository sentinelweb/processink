package speecher.interactor.sentence

import kotlinx.serialization.Serializable
import speecher.domain.Sentence

@Serializable
data class SentencesData constructor(
    var movieFilePaths: List<String> = listOf(),
    var wordsFilePathss: List<String> = listOf(),
    var sentences: Map<String, Sentence>? = null
)