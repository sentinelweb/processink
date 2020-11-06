package speecher.editor

import speecher.domain.Subtitles
import java.io.File

data class EditorState(
    var isDirty: Boolean = false,
    var movieFile: File? = null,
    var srtReadFile: File? = null,
    var srtRead: Subtitles? = null,
    var srtWriteFile: File? = null,
    var srtWrite: Subtitles? = null,
    var movieDurationSec: Float? = null,
    var moviePositionSec: Float? = null,
    var currentReadIndex: Int = -1,
    var currentWriteIndex: Int = -1,
    var lastReadIndex: Int = -1,
    var lastWriteIndex: Int = -1,
    var loopStartSec: Float? = null,
    var loopEndSec: Float? = null
)