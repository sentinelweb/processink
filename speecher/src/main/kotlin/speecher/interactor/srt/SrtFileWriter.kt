package speecher.interactor.srt

import speecher.domain.Subtitles
import java.io.File

class SrtFileWriter constructor(
    private val mapper: SrtMapper
) {
    fun write(subs: Subtitles, f: File) {
        val writer = f.writer()
        subs.timedTexts.forEachIndexed { index, sub ->
            writer.write("$index\n")
            writer.write("${mapper.formatTime(sub.from)} --> ${mapper.formatTime(sub.to)}\n")
            writer.write(sub.text.joinToString("\n", postfix = "\n\n"))
        }
        writer.close()
    }
}
