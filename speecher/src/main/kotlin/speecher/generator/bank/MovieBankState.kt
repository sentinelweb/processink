package speecher.generator.bank

import speecher.domain.Sentence

data class MovieBankState constructor(
    var words: Sentence? = null,
    var loadingWord: Int = -1, // currently loading word
    var activeIndex: Int = -1,// currently playing player
    var playingWord: Int = -1,// currently playing word
    var looping: Boolean = false,
    var movieToWordMap: MutableMap<Int, Sentence.Word?> = mutableMapOf(),
    var volume: Float = 0f
)