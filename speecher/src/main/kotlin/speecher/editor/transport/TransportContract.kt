package speecher.editor.transport

import io.reactivex.Observable
import io.reactivex.subjects.Subject
import java.io.File
import javax.swing.JComponent

interface TransportContract {

    interface Presenter {
        val updateObservable: Observable<UiData>

    }

    interface View {
        val events: Subject<UiEvent>
        val component: JComponent
        fun showWindow()
        var presenter: TransportPresenter
    }

    interface External {
        var speed: Float

        fun events(): Observable<UiEvent>
        fun setTitle(title: String)
        fun setStateListener(listener: StateListener)
        fun showOpenDialog(title: String, chosen: (File) -> Unit)
        fun showSaveDialog(title: String, chosen: (File) -> Unit)
        fun setDuration(dur: Float)
        fun setPosition(pos: Float)
        fun setPlayState(mode: UiDataType)
        fun setVolume(volume: Float)
        fun updateState()
    }

    interface StateListener {
        fun speed(speed: Float)
    }

    data class UiEvent constructor(
        val uiEventType: UiEventType,
        val data: Any? = null
    )

    enum class UiEventType {
        PLAY,
        PAUSE,
        FWD,
        REW,
        NEXT,
        LAST,
        LOOP,
        SEEK,
        FINE_SEEK,
        VOLUME_CHANGED,
        MUTE,
        MENU_FILE_NEW_SRT_WRITE,
        MENU_FILE_OPEN_MOVIE,
        MENU_FILE_OPEN_SRT_READ,
        MENU_FILE_OPEN_SRT_WRITE,
        MENU_FILE_SAVE_SRT,
        MENU_FILE_EXIT,
        MENU_EDIT_CUT,
        MENU_EDIT_COPY,
        MENU_EDIT_PASTE
    }

    data class UiData constructor(
        val uiDataType: UiDataType,
        val data: Any? = null
    )

    enum class UiDataType {
        TITLE, // String
        READ_SRT,// String
        WRITE_SRT,// String
        SPEED, // Int
        MODE_PLAYING, // boolean
        MODE_PAUSED, // boolean
        VOLUME, // 0f .. 1f
        MUTED, // boolean
        WORD, // String
        DURATION, // String
        POSITION, // String
        POSITION_SLIDER // 0f .. 1f
    }
}