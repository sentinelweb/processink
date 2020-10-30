package speecher.editor.transport

import io.reactivex.Observable
import io.reactivex.subjects.Subject

interface TransportContract {

    interface Presenter {
        val updateObservable: Observable<UiData>
    }

    interface View {
        val events: Subject<UiEvent>
    }

    interface External {
        fun events(): Observable<UiEvent>
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
        SEEK,
        FINE_SEEK,
        VOLUME_CHANGED,
        MUTE,
        MENU_FILE_NEW,
        MENU_FILE_OPEN,
        MENU_FILE_SAVE,
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
        SPEED, // Int
        MODE_PLAYING, // boolean
        MODE_PAUSED, // boolean
        VOLUME, // 0 .. 1
        MUTED, // boolean
        WORD, // String
    }
}