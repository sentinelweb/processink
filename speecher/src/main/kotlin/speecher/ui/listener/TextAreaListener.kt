package speecher.ui.listener

import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class TextAreaListener constructor(private val change: (String) -> Unit) : DocumentListener {
    override fun insertUpdate(e: DocumentEvent) {
        change(e.document.getText(0, e.document.length))
    }

    override fun removeUpdate(e: DocumentEvent) {
        change(e.document.getText(0, e.document.length))
    }

    override fun changedUpdate(e: DocumentEvent) {
        change(e.document.getText(0, e.document.length))
    }

}