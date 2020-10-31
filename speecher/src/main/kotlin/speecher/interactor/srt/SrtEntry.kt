package speecher.interactor.srt

data class SrtEntry constructor(
    val index: Int,
    var timeLine: String? = null,
    val text: MutableList<String> = mutableListOf()
)