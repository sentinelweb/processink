package speecher.util.serialization

import kotlinx.serialization.*
import java.awt.Color

@Serializer(forClass = Color::class)
object ColorSerializer : KSerializer<Color> {

    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("WithCustomDefault", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Color) {
        encoder.encodeInt(makeInt(value))
    }

    fun makeInt(value: Color) = value.rgb

    override fun deserialize(decoder: Decoder): Color {
        val decodeInt = decoder.decodeInt()
        println("int:" + decodeInt)
        return parseInt(decodeInt)
    }

    fun parseInt(decodeInt: Int) = Color(decodeInt, true)

}