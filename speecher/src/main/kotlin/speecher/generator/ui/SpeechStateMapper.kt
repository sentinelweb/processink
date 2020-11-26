package speecher.generator.ui

import com.google.gson.Gson

//fun SpeechState.serialise() = JSON.stringify(
//    SpeechState.serializer(), this
//)

class SpeechStateMapper(private val gson: Gson) {

    fun deserializeSpeechState(input: String) = gson.fromJson(input, SpeechState::class.java)

    fun serializeSpeechState(state: SpeechState) =
        gson.toJson(state.copy(subs = null, subsDisplay = null, wordListWithCursor = listOf()))
//  fun deserializeSpeechState(input: String) = JSON.parse(
//        SpeechState.serializer(), input.toString()
//    )

}