package speecher.domain

data class Sentence(
    val words: List<Word>
) {
    data class Word(
        val sub: Subtitles.Subtitle,
        val spaceBefore: Float = 0f,
        val spaceAfter: Float = 0f,
        val speed: Float = 1f,
        val vol: Float = 1f
    )
}