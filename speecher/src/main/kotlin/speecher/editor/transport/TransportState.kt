package speecher.editor.transport

data class TransportState(
    var isDirty: Boolean = false,
    var speed: Float = 1f,
    var volume: Float = 0.2f,
    var muted: Boolean = false,
    var posSec: Float = 0.0f,
    var durSec: Float = 0.0f,
    var positionLastUpdate: Long = 0
)