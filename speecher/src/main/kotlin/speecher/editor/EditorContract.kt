package speecher.editor

import speecher.editor.transport.TransportContract
import java.io.File

interface EditorContract {

    interface Presenter {
        fun duration(dur: Float)
        fun position(pos: Float)
        fun setPlayState(mode: TransportContract.UiDataType)
    }

    interface View {
        fun openMovie(file: File)
        fun setMovieSpeed(speed: Float)
        fun play()
        fun pause()
        fun volume(vol: Float)
        fun seekTo(positionSec: Float)
    }

}