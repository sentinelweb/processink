package speecher.generator.ui.sentence_list

import speecher.domain.Sentence

data class SentenceListState constructor(
    val sentences: MutableMap<String, Sentence> = mutableMapOf(),
    var selectedKey: String? = null
)