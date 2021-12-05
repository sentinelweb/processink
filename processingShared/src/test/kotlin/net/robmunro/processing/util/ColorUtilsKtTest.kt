package net.robmunro.processing.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.awt.Color

class ColorUtilsKtTest {

    @Test
    fun encodeARGB() {
        val actual = Color.RED.encodeARGB()
        assertEquals(actual.decodeARGB(), Color.RED)
    }

    @Test
    fun decodeARGB() {
        assertEquals("#ffff0000".decodeARGB(), Color.RED)
    }
}