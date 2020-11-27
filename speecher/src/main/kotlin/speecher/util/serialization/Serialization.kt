package speecher.util.serialization

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.serializersModuleOf
import speecher.domain.Sentence
import speecher.domain.Subtitles
import speecher.interactor.sentence.SentencesData
import java.awt.Color
import java.awt.Font
import java.io.File

fun Sentence.serialise() = JSON.stringify(
    Sentence.serializer(), this
)

fun Subtitles.serialise() = JSON.stringify(
    Subtitles.serializer(), this
)

fun Font.serialise() = JSON.stringify(
    FontSerializer, this
)

fun deserializeFont(input: String) = JSON.parse(
    FontSerializer, input
)

fun Color.serialise() = JSON.stringify(
    ColorSerializer, this
)

fun deserializeColor(input: Int) = JSON.parse(
    ColorSerializer, input.toString()
)

internal val JSON = Json(
    JsonConfiguration.Stable.copy(prettyPrint = true, ignoreUnknownKeys = true, isLenient = true),
    context = serializersModuleOf(
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
    )
)
