package speecher.generator.ui

import speecher.domain.Sentence
import speecher.domain.Subtitles
import speecher.generator.ui.SpeechContract.SortOrder.NATURAL
import java.awt.Color
import java.awt.Font
import java.io.File

//@Serializable
data class SpeechState(
    var wordSentence: List<Sentence.Word> = listOf(),
    /*@ContextualSerialization*/ var movieFile: File? = null,
    /*@ContextualSerialization*/ var srtWordFile: File? = null,
    /*@ContextualSerialization*/ var sentencesFile: File? = null,
    var words: Subtitles? = null,
    var wordsDisplay: List<Subtitles.Subtitle>? = null,
    var cursorPos: Int = 0,
    var searchText: String? = null,
    var sortOrder: SpeechContract.SortOrder = NATURAL,
    /*@ContextualSerialization*/ var selectedFontColor: Color? = null,
    /*@ContextualSerialization*/ var selectedFont: Font? = null,
    var volume: Float = 0f,
    var previewVolume: Float = 1.0f,
    var playEventLatency: Float? = 0.05f,
    var editingWord: Int? = null,
    var wordSentenceWithCursor: MutableList<Sentence.Word> = mutableListOf(),
    var wordSelection: MutableMap<Int, Sentence.Word> = mutableMapOf(),
    var clipboard: List<Sentence.Word>? = null,
    var currentSentenceId: String? = null,
    var previewingWord: Int? = null
// var speakString: String? = "This is the year in which we mourn the passing of thought humanity dissenters and media to the applause of crowds",

)