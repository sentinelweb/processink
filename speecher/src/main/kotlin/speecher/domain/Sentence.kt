package speecher.domain

data class Sentence(
    val items: List<Item>
) {
    data class Item(
        val sub: Subtitles.Subtitle,
        val spaceBefore: Float = 0f,
        val spaceAfter: Float = 0f,
        val speed: Float = 1f
    )
}