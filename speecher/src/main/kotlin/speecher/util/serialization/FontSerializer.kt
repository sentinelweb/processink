package speecher.util.serialization

import kotlinx.serialization.*
import java.awt.Font

@Serializer(forClass = Font::class)
object FontSerializer : KSerializer<Font> {

    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("WithCustomDefault", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Font) {
        val value1 = "${value.fontName}:${value.style}:${value.size}"
        encoder.encodeString(value1)
    }

    override fun deserialize(decoder: Decoder): Font =
        decoder.decodeString().split(':').let {
            Font(it[0], it[1].toInt(), it[2].toInt())
        }
}