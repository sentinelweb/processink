package cubes.util.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.awt.Color

@Serializer(forClass = Color::class)
object ColorSerializer : KSerializer<Color> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("WithCustomDefault", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Color) {
        encoder.encodeInt(makeInt(value))
    }

    fun makeInt(value: Color) = value.rgb

    override fun deserialize(decoder: Decoder): Color {
        val decodeInt = decoder.decodeInt()
//        println("int:" + decodeInt)
        return parseInt(decodeInt)
    }

    fun parseInt(decodeInt: Int) = Color(decodeInt, true)

}