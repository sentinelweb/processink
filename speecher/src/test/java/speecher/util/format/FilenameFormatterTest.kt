package speecher.util.format

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class FilenameFormatterTest {

    private val sut: FilenameFormatter = FilenameFormatter()
    private val fixtBase = "/x/x/x/ff"

    @Test
    fun movieToRead() {
        assertEquals(
            "$fixtBase.en.srt",
            sut.movieToRead("$fixtBase.mp4")
        )
    }

    @Test
    fun movieToWords() {
        assertEquals(
            "$fixtBase.words.srt",
            sut.movieToWords("$fixtBase.mp4")
        )
    }

    @Test
    fun movieToSentence() {
        assertEquals(
            "$fixtBase.sentence.json",
            sut.movieToSentence("$fixtBase.mp4")
        )
    }

    @Test
    fun wordsToSentence() {
        assertEquals(
            "$fixtBase.sentence.json",
            sut.wordsToSentence("$fixtBase.words.srt")
        )
    }

    @Test
    fun testMovieToRead() {
        assertEquals(
            File("$fixtBase.en.srt"),
            sut.movieToRead(File("$fixtBase.mp4"))
        )
    }

    @Test
    fun testMovieToWords() {
        assertEquals(
            File("$fixtBase.words.srt"),
            sut.movieToWords(File("$fixtBase.mp4"))
        )
    }

    @Test
    fun testMovieToSentence() {
        assertEquals(
            File("$fixtBase.sentence.json"),
            sut.movieToSentence(File("$fixtBase.mp4"))
        )
    }

    @Test
    fun testWordsToSentence() {
        assertEquals(
            File("$fixtBase.sentence.json"),
            sut.wordsToSentence(File("$fixtBase.words.srt"))
        )
    }
}