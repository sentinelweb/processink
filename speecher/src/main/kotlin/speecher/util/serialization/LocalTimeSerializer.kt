package speecher.util.serialization

import kotlinx.serialization.*
import java.time.LocalTime

@Serializer(forClass = LocalTime::class)
object LocalTimeSerializer : KSerializer<LocalTime> {

    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("WithCustomDefault", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalTime {
        return LocalTime.parse(decoder.decodeString())
    }
}