package speecher.generator

import speecher.domain.Subtitles
import java.io.File

data class GeneratorState constructor(
    var movieFile: File? = null,
    var srtFile: File? = null,
    var subs: Subtitles? = null,
    var words: List<Subtitles.Subtitle> = listOf(),
    var startTime: Long = 0,
    var speakString: String = "This is the year in which we mourn the passing of thought humanity dissenters and media to the applause of crowds",
    var playIndex: Int = -1,
    var loadIndex: Int = -1
)