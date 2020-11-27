package speecher.util.format

import java.io.File

class FilenameFormatter {

    fun movieToRead(name: String) = name.substring(0, name.lastIndexOf('.')).plus(DEF_READ_SRT_EXT)
    fun movieToWords(name: String) = name.substring(0, name.lastIndexOf('.')).plus(DEF_WORDS_SRT_EXT)
    fun movieToSentence(name: String) = name.substring(0, name.lastIndexOf('.')).plus(DEF_SENTENCE_EXT)
    fun wordsToSentence(name: String) = name.substring(0, name.length - DEF_WORDS_SRT_EXT.length).plus(DEF_SENTENCE_EXT)

    fun movieToRead(file: File) = File(movieToRead(file.absolutePath))
    fun movieToWords(file: File) = File(movieToWords(file.absolutePath))
    fun movieToSentence(file: File) = File(movieToSentence(file.absolutePath))
    fun wordsToSentence(file: File) = File(wordsToSentence(file.absolutePath))

    companion object {
        var DEF_MOVIE_EXT = ".mp4"
        var DEF_READ_SRT_EXT = ".en.srt"
        var DEF_WORDS_SRT_EXT = ".words.srt"
        var DEF_SENTENCE_EXT = ".sentence.json"
    }
}