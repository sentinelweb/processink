package speecher.generator.ui

import com.google.gson.Gson
import speecher.domain.Sentence
import speecher.generator.ui.SpeechContract.CursorPosition.*
import speecher.generator.ui.SpeechContract.SortOrder.*
import speecher.interactor.sentence.SentencesData
import kotlin.math.min

//fun SpeechState.serialise() = JSON.stringify(
//    SpeechState.serializer(), this
//)

class SpeechStateMapper(private val gson: Gson) {

    fun deserializeSpeechState(input: String) = gson.fromJson(input, SpeechState::class.java)

    fun serializeSpeechState(state: SpeechState) =
        gson.toJson(
            state.copy(
                words = null,
                wordsDisplay = null,
                wordSentenceWithCursor = mutableListOf()
            )
        )
//  fun deserializeSpeechState(input: String) = JSON.parse(
//        SpeechState.serializer(), input.toString()
//    )

    fun mapSentenceData(state: SpeechState, sentences: Map<String, Sentence>): SentencesData = SentencesData(
        movieFilePaths = state.movieFile?.let { listOf(it.absolutePath) } ?: listOf(),
        wordsFilePathss = state.srtWordFile?.let { listOf(it.absolutePath) } ?: listOf(),
        sentences = sentences
    )

    fun updateWords(state: SpeechState) {
        val subs = state.searchText
            ?.let { searchText ->
                state.words?.timedTexts?.filter { it.text[0].contains(searchText) }
            } ?: state.words?.timedTexts

        when (state.sortOrder) {
            NATURAL -> state.wordsDisplay = subs
            A_Z -> state.wordsDisplay = subs?.sortedBy { it.text[0].toLowerCase() }
            Z_A -> state.wordsDisplay = subs?.sortedBy { it.text[0].toLowerCase() }?.reversed()
        }
    }

    fun buildWordListFromString(state: SpeechState, s: String) {
        state.wordSentence = state.wordSentence.toMutableList().apply {
            val elements = s
                .split(" ")
                .map { word -> state.words?.timedTexts?.find { it.text[0] == word } }
                .filterNotNull()
            val elements1 = elements.map { Sentence.Word(it) }
            addAll(state.cursorPos, elements1)
            state.cursorPos = elements1.size
        }
    }

    fun wordSelection(state: SpeechState): Map<Int, Sentence.Word>? =
        if (state.wordSelection.size > 0) {
            state.wordSelection
        } else if (state.cursorPos + 1 < state.wordSentenceWithCursor.size) {
            mapOf(state.cursorPos + 1 to state.wordSentenceWithCursor[state.cursorPos + 1])
        } else null

    fun newSentence(state: SpeechState) {
        state.apply {
            wordSentence = listOf()
            cursorPos = 0
            editingWord = null
            wordSelection = mutableMapOf()
            currentSentenceId = null
        }
    }

    fun cursorOperaation(op: SpeechContract.CursorPosition, state: SpeechState) {
        when (op) {
            START -> state.cursorPos = 0
            LAST -> state.cursorPos = Integer.max(0, state.cursorPos - 1)
            NEXT -> state.cursorPos = min(state.wordSentence.size, state.cursorPos + 1)
            END -> state.cursorPos = state.wordSentence.size
        }
    }
}