package speecher.util.serialization

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.awt.Font

class FontSerializerTest {

    val font: Font = Font("Arial", Font.BOLD, 25)

    @Before
    fun setUp() {
    }

    @Test
    fun serialize() {
        assertEquals("\"Arial:1:25\"", font.serialise())
    }

    @Test
    fun deserialize() {
        assertEquals(font, deserializeFont("\"Arial:1:25\""))
    }
}