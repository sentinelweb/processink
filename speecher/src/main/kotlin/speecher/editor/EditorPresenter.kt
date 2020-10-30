package speecher.editor

import speecher.editor.transport.TransportPresenter

class EditorPresenter constructor(
    private val view: EditorContract.View,
    private val state: EditorState,
    private val transport: TransportPresenter

) : EditorContract.Presenter {

}