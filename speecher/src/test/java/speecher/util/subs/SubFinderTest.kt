package speecher.util.subs

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import speecher.domain.Subtitles

class SubFinderTest {

    val sut = SubFinder()

    val testData = Subtitles(
        timedTexts = listOf(
            Subtitles.Subtitle(1f, 2f, listOf("Une")),
            Subtitles.Subtitle(2f, 3f, listOf("Deux")),
            Subtitles.Subtitle(3f, 4f, listOf("Troi")),
            Subtitles.Subtitle(4f, 5f, listOf("Carte")),
            Subtitles.Subtitle(5f, 6f, listOf("Cinq")),
            Subtitles.Subtitle(6f, 7f, listOf("Six"))
        )
    )

    @Before
    fun setUp() {
    }

    @Test
    fun findOverlapping() {
        val find = Subtitles.Subtitle(3.5f, 4.5f, listOf("Troi"))
        val actual = sut.findOverlapping(find, testData.timedTexts)
        assertEquals(2, actual)

        val find1 = Subtitles.Subtitle(2.5f, 3.5f, listOf("Troi"))
        val actual1 = sut.findOverlapping(find1, testData.timedTexts)
        assertEquals(2, actual1)

        val find2 = Subtitles.Subtitle(3f, 4f, listOf("Troi"))
        val actual2 = sut.findOverlapping(find2, testData.timedTexts)
        assertEquals(2, actual2)

        val find3 = Subtitles.Subtitle(2f, 2.9f, listOf("Troi"))
        val actual3 = sut.findOverlapping(find3, testData.timedTexts)
        assertNull(actual3)

        val find4 = Subtitles.Subtitle(4.1f, 5f, listOf("Troi"))
        val actual4 = sut.findOverlapping(find4, testData.timedTexts)
        assertNull(actual4)

        val find5 = Subtitles.Subtitle(3f, 4f, listOf("Une"))
        val actual5 = sut.findOverlapping(find5, testData.timedTexts)
        assertNull(actual5)

    }


    @Test
    fun getRangeInclusive() {
        val find = Subtitles.Subtitle(3f, 4f, listOf("xxx"))
        val actual = sut.getRangeInclusive(find, testData)
        assertEquals(3, actual.size)
    }

    @Test
    fun testGetRangeInclusive() {
        val actual = sut.getRangeInclusive(3f, 4f, testData)
        assertEquals(3, actual.size)
    }

    @Test
    fun getRangeExclusive() {
        val find = Subtitles.Subtitle(3f, 4f, listOf("xxx"))
        val actual = sut.getRangeExclusive(find, testData)
        assertEquals(1, actual.size)
    }

    @Test
    fun testGetRangeExclusive() {
        val actual = sut.getRangeExclusive(3f, 4f, testData)
        assertEquals(1, actual.size)
    }

    @Test
    fun buildMapSimple() {
        val readList = "thank you very much thank you everyone who stood up unnecessary but very flattering".split(" ")
        fun String.buildSub() = this.split(" ").map { Subtitles.Subtitle(0f, 0f, listOf(it)) }

        val actual1 = sut.buildMapSimple(readList, "thank you everyone".buildSub())
        assertEquals(mapOf(Pair(4, 0), Pair(5, 1), Pair(6, 2)), actual1)

        val actual2 = sut.buildMapSimple(readList, "everyone who stood up unnecessary".buildSub())
        assertEquals(mapOf(Pair(6, 0), Pair(7, 1), Pair(8, 2), Pair(9, 3), Pair(10, 4)), actual2)

        val actual3 = sut.buildMapSimple(readList, "everyone who stood unnecessary".buildSub()) // missing up
        assertEquals(mapOf<Int, Int>(), actual3)


    }

    @Test
    fun testCorrelate() {
        val readList = "thank you very much thank you everyone who stood up unnecessary but very flattering".split(" ")
        printCorrelateData(readList, "thank you everyone".split(" "))
        printCorrelateData(readList, "thank you everyone stood".split(" "))
        printCorrelateData(readList, "thank you everyone stood up".split(" "))
        printCorrelateData(readList, "thank you everyone stood up".split(" "))
        printCorrelateData(readList, "thank you everyone stood up but very flattering".split(" "))
        printCorrelateData(readList, "thank you everyone stood up unnecessary but very".split(" "))
    }

    private fun printCorrelateData(rList: List<String>, wList: List<String>) {
        val scores = sut.correlate(rList, wList)
        println(
            "fwd: $scores sLen = ${scores.size} rLen = ${rList.size} wLen = ${wList.size} max = ${scores.maxOrNull()} maxIndex = ${
                scores.indexOf(
                    scores.maxOrNull()
                )
            } "
        )
    }

    @Test
    fun labels() {
        run loop@{
            (0..5).forEach read@{ position ->
                println("position = $position")
                (0..8).forEach { wposition ->
                    println("wposition = $wposition")
                    //return@read
                }
                return@loop
            }
        }
        println("done")
    }
}