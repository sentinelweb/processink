package speecher.generator.ui.sentence_list

import speecher.domain.Sentence

data class SentenceListState constructor(
    var sentences: Map<String, Sentence>? = null,
    var selectedKey: String? = null
)