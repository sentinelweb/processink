package speecher.util.serialization

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import speecher.domain.Sentence
import speecher.domain.Subtitles
import speecher.interactor.sentence.SentencesData
import java.awt.Color
import java.awt.Font
import java.io.File

fun Sentence.serialise() = JSON.encodeToString(
    Sentence.serializer(), this
)

fun Subtitles.serialise() = JSON.encodeToString(
    Subtitles.serializer(), this
)

fun Font.serialise() = JSON.encodeToString(
    FontSerializer, this
)

fun deserializeFont(input: String) = JSON.decodeFromString(
    FontSerializer, input
)

fun Color.serialise() = JSON.encodeToString(
    ColorSerializer, this
)

fun deserializeColor(input: Int) = JSON.decodeFromString(
    ColorSerializer, input.toString()
)

internal val JSON = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
    serializersModule = SerializersModule {
        mapOf(
            Sentence::class to Sentence.serializer(),
            Subtitles::class to Subtitles.serializer(),
            Sentence.Word::class to Sentence.Word.serializer(),
            Subtitles.Subtitle::class to Subtitles.Subtitle.serializer(),
            Font::class to FontSerializer,
            Color::class to ColorSerializer,
            SentencesData::class to SentencesData.serializer(),
            File::class to FileSerializer
        )
    }
}

