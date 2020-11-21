package speecher.util.format

import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TimeFormatter {
    private val timeStampFormatter = DateTimeFormatter.ofPattern(TIME_PATTERN)

    fun formatTime(date: LocalTime): String =
        timeStampFormatter.format(date).strip00()

    fun formatTime(timeSecs: Float): String =
        timeStampFormatter.format(LocalTime.ofNanoOfDay((timeSecs * 1_000_000_000).toLong())).strip00()

    private fun String.strip00() = this.let {
        var formatted = it
        while (formatted.startsWith("00:")) {
            formatted = formatted.substring(3)
        }
        formatted
    }

    companion object {
        // 00:00:36.150
        private const val TIME_PATTERN = "HH:mm:ss.SSS"
    }
}