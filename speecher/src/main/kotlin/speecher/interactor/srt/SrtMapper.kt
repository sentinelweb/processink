package speecher.interactor.srt

import speecher.domain.Subtitles
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class SrtMapper {

    private val timeStampFormatter = DateTimeFormatter.ofPattern(TIME_PATTERN)

    fun parseTime(dateString: String): LocalTime =
        LocalTime.parse(dateString, timeStampFormatter)

    fun formatTime(date: LocalTime): String =
        timeStampFormatter.format(date)

    fun formatTime(sec: Float): String = formatTime(LocalTime.ofNanoOfDay((sec * 1_000_000_000f).toLong()))

    fun map(items: List<SrtEntry>) = Subtitles(
        items.map { entry ->
            val (start, end) = entry.timeLine?.split("-->") ?: throw IllegalArgumentException("")
            Subtitles.Subtitle(
                toSec(parseTime(start.trim())),
                toSec(parseTime(end.trim())),
                entry.text.filter { it.isNotBlank() })
        }
    )

    fun toSec(time: LocalTime): Float = time.toNanoOfDay() / 1_000_000_000f

    companion object {
        // 00:00:36,150
        private const val TIME_PATTERN = "HH:mm:ss,SSS"
    }
}