package speecher.generator

import io.reactivex.disposables.CompositeDisposable
import speecher.domain.Sentence
import java.io.File

data class GeneratorState constructor(
    var movieFile: File? = null,
    var words: Sentence? = null,
    var startTime: Long = 0,
    var wordIndex: Int = -1, // currently playing word
    var activeIndex: Int = -1,
    var playingWord: Int = -1,
    val disposables: CompositeDisposable = CompositeDisposable()

)