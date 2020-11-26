package speecher.util.serialization

import kotlinx.serialization.*
import java.io.File

@Serializer(forClass = File::class)
object FileSerializer : KSerializer<File> {

    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("WithCustomDefault", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: File) {
        encoder.encodeString(makeString(value))
    }

    fun makeString(value: File) = value.absolutePath

    override fun deserialize(decoder: Decoder): File {
        val decodeString = decoder.decodeString()
        return parseString(decodeString)
    }

    fun parseString(decodeString: String) = File(decodeString)
}