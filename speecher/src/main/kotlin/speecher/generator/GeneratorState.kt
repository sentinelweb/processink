package speecher.generator

import io.reactivex.disposables.CompositeDisposable
import speecher.domain.Sentence

data class GeneratorState(
    var words: Sentence? = null,
    var previewWord: Sentence.Word? = null,
    val disposables: CompositeDisposable = CompositeDisposable()
)