package speecher.domain

import kotlinx.serialization.Serializable

@Serializable
data class Subtitles constructor(
    val timedTexts: List<Subtitle>,
    val lang: String = "en"
) {
    @Serializable
    data class Subtitle constructor(
        val fromSec: Float,
        val toSec: Float,
        val text: List<String>

    ) {
        fun between(sec: Float): Boolean =
            fromSec < sec && toSec > sec
    }
}