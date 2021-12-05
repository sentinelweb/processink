package cubes.osc

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class OscTypeTagsParserTest {

    private lateinit var sut: OscTypeTagsParser

    @Before
    fun setUp() {
        sut = OscTypeTagsParser()
    }

    @Test
    fun `parse fsd(fff)`() {

        val actual = sut.parse("fsd[fff]")

        assertEquals(listOf("f", "s", "d", "[fff]"), actual)
    }
}