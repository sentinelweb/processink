package speecher.generator.ui

import io.reactivex.disposables.CompositeDisposable
import speecher.domain.Sentence
import speecher.domain.Subtitles
import java.io.File

data class SpeechState(
    var wordList: List<Sentence.Word> = listOf(),
    var srtFile: File? = null,
    var subs: Subtitles? = null,
    var subsDisplay: List<Subtitles.Subtitle>? = null,
    var cursorPos: Int = 0,
    var searchText: String? = null,
    var sortOrder: SpeechContract.SortOrder = SpeechContract.SortOrder.NATURAL,
    val disposables: CompositeDisposable = CompositeDisposable(),
    var speakString: String? = "This is the year in which we mourn the passing of thought humanity dissenters and media to the applause of crowds"
)