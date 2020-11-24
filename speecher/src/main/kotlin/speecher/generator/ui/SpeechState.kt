package speecher.generator.ui

import io.reactivex.disposables.CompositeDisposable
import speecher.domain.Sentence
import speecher.domain.Subtitles
import speecher.generator.ui.SpeechContract.SortOrder.NATURAL
import java.awt.Color
import java.awt.Font
import java.io.File

data class SpeechState(
    var wordList: List<Sentence.Word> = listOf(),
    var srtFile: File? = null,
    var subs: Subtitles? = null,
    var subsDisplay: List<Subtitles.Subtitle>? = null,
    var cursorPos: Int = 0,
    var searchText: String? = null,
    var sortOrder: SpeechContract.SortOrder = NATURAL,
    var selectedFontColor: Color? = null,
    var selectedFont: Font? = null,
    var volume: Float = 0f,
    val disposables: CompositeDisposable = CompositeDisposable(),
    var speakString: String? = "This is the year in which we mourn the passing of thought humanity dissenters and media to the applause of crowds",
    var playEventLatency: Float? = 0.05f,
    var selectedWord: Int? = null,
    var wordListWithCursor: List<Sentence.Word> = listOf()
)