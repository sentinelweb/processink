package speecher.util.serialization

import cubes.CubesState
import cubes.models.Cube
import cubes.models.CubeList
import cubes.models.Shape
import cubes.models.TextList
import cubes.util.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import processing.core.PVector
import java.awt.Color
import java.awt.Font
import java.io.File
import java.time.LocalTime


fun Font.serialise() = stateJsonSerializer.encodeToString(
    FontSerializer, this
)

fun deserializeFont(input: String) = stateJsonSerializer.decodeFromString(
    FontSerializer, input
)

fun Color.serialise() = stateJsonSerializer.encodeToString(
    ColorSerializer, this
)

fun deserializeColor(input: Int) = stateJsonSerializer.decodeFromString(
    ColorSerializer, input.toString()
)

val stateJsonSerializer = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
    classDiscriminator = "domainType"// property added when base domain type is use (see ResponseDomain)
    serializersModule = SerializersModule {
        mapOf(
            CubesState::class to CubesState.serializer(),
            CubeList::class to CubeList.serializer(),
            TextList::class to TextList.serializer(),
            TextList.Text::class to TextList.Text.serializer(),
            Shape::class to Shape.serializer(),
            CubeList::class to CubeList.serializer(),
            Cube::class to Cube.serializer()
        )
        // todo enable for shapes array
//        polymorphic(Domain::class, PlaylistDomain::class, PlaylistDomain.serializer())
    }.plus(SerializersModule {
        contextual(Color::class, ColorSerializer)
    }
    ).plus(SerializersModule {
        contextual(Font::class, FontSerializer)
    }
    ).plus(SerializersModule {
        contextual(File::class, FileSerializer)
    }
    ).plus(SerializersModule {
        contextual(PVector::class, PVectorSerializer)
    }
    ).plus(SerializersModule {
        contextual(LocalTime::class, LocalTimeSerializer)
    }
    )
}

