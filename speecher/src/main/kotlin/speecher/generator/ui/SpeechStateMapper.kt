package speecher.generator.ui

import speecher.util.serialization.JSON

fun SpeechState.serialise() = JSON.stringify(
    SpeechState.serializer(), this
)

class SpeechStateMapper {

    fun deserializeSpeechState(input: String) = JSON.parse(
        SpeechState.serializer(), input.toString()
    )

}