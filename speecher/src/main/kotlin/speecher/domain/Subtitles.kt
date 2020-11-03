package speecher.domain

data class Subtitles constructor(
    val timedTexts: List<Subtitle>,
    val lang: String = "en"
) {
    data class Subtitle constructor(
        val fromSec: Float,
        val toSec: Float,
        val text: List<String>


    ) {
        fun between(sec: Float): Boolean =
            fromSec < sec && toSec > sec
    }
}