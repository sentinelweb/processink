package speecher.util.serialization

import kotlinx.serialization.*
import java.awt.Font

@Serializer(forClass = Font::class)
object FontSerializer : KSerializer<Font> {

    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("WithCustomDefault", PrimitiveKind.STRING)

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