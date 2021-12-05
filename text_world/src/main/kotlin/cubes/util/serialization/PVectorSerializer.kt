package cubes.util.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import processing.core.PVector

@Serializer(forClass = PVector::class)
object PVectorSerializer : KSerializer<PVector> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("WithCustomDefault", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: PVector) {
        value.run { "${x}:${y}:${z}" }
//            .apply { println("pvec:" + this) }
            .apply { encoder.encodeString(this) }

    }

    override fun deserialize(decoder: Decoder): PVector {
        return decoder.decodeString()
//            .apply { println("pvec:" + this) }
            .run { split(":") }
            .takeIf { it.size == 3 }
            ?.run { PVector(get(0).toFloat(), get(1).toFloat(), get(2).toFloat()) }
            ?: PVector()
    }


}