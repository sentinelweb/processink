package speecher.util.serialization

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.awt.Color

class ColorSerializerTest {

    private val color: Color = Color(1, 2, 3, 4)

    private val serialiseInt = 67174915

    @Before
    fun setUp() {
    }

    @Test
    fun serialize() {
        //println(color.serialise())
        assertEquals(serialiseInt.toString(), color.serialise())
    }

    @Test
    fun deserialize() {
        assertEquals(color, deserializeColor(serialiseInt))
    }

    @Test
    fun testColor() {
        assertEquals(Color(serialiseInt, true), Color(1, 2, 3, 4))
    }
}