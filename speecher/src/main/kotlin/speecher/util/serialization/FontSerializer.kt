package speecher.util.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.awt.Font

@Serializer(forClass = Font::class)
object FontSerializer : KSerializer<Font> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("WithCustomDefault", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Font) {
        val value1 = makeString(value)
        encoder.encodeString(value1)
    }

    fun makeString(value: Font) = "${value.family}:${value.style}:${value.size}"

    override fun deserialize(decoder: Decoder): Font =
        parseString(decoder.decodeString())

    fun parseString(str: String): Font = str.split(':').let {
        Font(it[0], it[1].toInt(), it[2].toInt())
    }

}