package cubes.util.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.File

@Serializer(forClass = File::class)
object FileSerializer : KSerializer<File> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("WithCustomDefault", PrimitiveKind.STRING)

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