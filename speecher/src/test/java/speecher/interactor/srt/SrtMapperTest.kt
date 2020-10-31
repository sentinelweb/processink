package speecher.interactor.srt

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalTime

class SrtMapperTest {

    lateinit var sut: SrtMapper

    @Before
    fun setUp() {
        sut = SrtMapper()
    }

    @After
    fun tearDown() {
    }

    @Test
    fun parseDate() {
        val input = "02:42:36,150"
        val actual = sut.parseTime(input)
        assertEquals(actual.hour, 2)
        assertEquals(actual.minute, 42)
        assertEquals(actual.second, 36)
        assertEquals(actual.nano, 150 * 1_000_000)
    }

    @Test
    fun formatDate() {
        val input = LocalTime.of(2, 3, 4, 5 * 1_000_000)
        val actual = sut.formatTime(input)
        assertEquals(actual, "02:03:04,005")
    }
}