package cubes.util.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.robmunro.processing.util.decodeARGB
import net.robmunro.processing.util.encodeARGB
import java.awt.Color

@Serializer(forClass = Color::class)
object ColorSerializer : KSerializer<Color> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("WithCustomDefault", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Color) {
//        encoder.encodeInt(makeInt(value))
        encoder.encodeString(value.encodeARGB())
    }

    //fun makeInt(value: Color) = value.rgb

    override fun deserialize(decoder: Decoder): Color {
//        return try {
//            parseInt(decoder.decodeInt())
//        } catch(nx:Exception){
//            decoder.decodeString().decodeARGB()
//        }
        return decoder.decodeString().decodeARGB()
    }

    fun parseInt(decodeInt: Int) = Color(decodeInt, true)

}