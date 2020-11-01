package speecher.editor.transport

data class TransportState(
    var isDirty: Boolean = false,
    var speed: Float = 1f,
    var volume: Float = 1f,
    var muted: Boolean = false
)