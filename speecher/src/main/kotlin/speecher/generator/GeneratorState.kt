package speecher.generator

import io.reactivex.disposables.CompositeDisposable
import speecher.domain.Sentence
import java.io.File

data class GeneratorState constructor(
    var movieFile: File? = null,
    var words: Sentence? = null,
    var startTime: Long = 0,
    var wordIndex: Int = -1, // currently loading word
    var activeIndex: Int = -1,// currently playing player
    var playingWord: Int = -1,// currently playing word
    val disposables: CompositeDisposable = CompositeDisposable(),
    var looping: Boolean = false,
    var movieToWordMap: MutableMap<Int, Sentence.Word?> = mutableMapOf(),
    var volume: Float = 0f
)