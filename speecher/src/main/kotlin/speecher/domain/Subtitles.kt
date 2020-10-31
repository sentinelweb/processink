package speecher.domain

import java.time.LocalTime

data class Subtitles constructor(
    val timedTexts: List<Subtitle>,
    val lang: String = "en"
) {
    data class Subtitle constructor(
        val from: LocalTime,
        val to: LocalTime,
        val text: List<String>
    )
}