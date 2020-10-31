package speecher.interactor.srt

import speecher.domain.Subtitles
import speecher.interactor.srt.SrtFileReader.LineType.*
import java.io.File

class SrtFileReader constructor(
    private val mapper: SrtMapper
) {

    private var items: MutableList<SrtEntry> = mutableListOf()
    private var currentItem: SrtEntry? = null
    private var lastLineType: LineType = NONE

    private enum class LineType { NONE, INDEX, TIME, TEXT, END }

    fun read(f: File): Subtitles {
        f.forEachLine { line ->
            when (lastLineType) {
                NONE -> {
                    checkForNewItem(line)
                }
                INDEX -> {
                    if (line.contains("-->") && currentItem != null) {
                        currentItem?.timeLine = line
                        lastLineType = TIME
                    }
                }
                TIME -> {
                    if (!line.isEmpty()) {
                        currentItem?.text?.add(line)
                        lastLineType = TEXT
                    }
                }
                TEXT -> {
                    if (!line.isEmpty()) {
                        currentItem?.text?.add(line)
                        lastLineType = TEXT
                    } else {
                        currentItem?.apply { items.add(this) }
                        currentItem = null
                        lastLineType = END
                    }
                }
                END -> {
                    checkForNewItem(line)
                }
            }
        }
        return mapper.map(items)
    }

    private fun checkForNewItem(line: String) {
        if (!line.isEmpty()) try {
            currentItem = SrtEntry(line.toInt())
            lastLineType = INDEX
        } catch (ex: Exception) {
            println("SRT Parse Error: Expected index .. ($line)")
        }
    }

}