package speecher.generator.ui

import speecher.ui.util.backgroundColor
import speecher.ui.util.style
import java.awt.BorderLayout
import java.awt.Dialog
import javax.swing.*
import javax.swing.border.EmptyBorder

class LoadingDialog {

    private var modelDialog: JDialog? = null

    fun showLoadingDialog(frame: JFrame) {
        if (modelDialog == null) {
            modelDialog = createDialog(frame)
        }

        modelDialog?.isVisible = true
    }

    fun hide() {
        modelDialog?.isVisible = false
    }

    private fun createDialog(frame: JFrame): JDialog {
        val modelDialog = JDialog(frame, "Loading ...", Dialog.ModalityType.DOCUMENT_MODAL)
        modelDialog.setBounds(132, 132, 400, 100)

        JPanel()
            .apply {
                background = backgroundColor
                layout = BorderLayout()
                border = EmptyBorder(20, 20, 20, 20)
                modelDialog.getContentPane().add(this)
                JLabel("Loading ... ")
                    .style()
                    .let {
                        add(it, BorderLayout.NORTH)
                    }
                JProgressBar().apply {
                    isIndeterminate = true
                    isBorderPainted = true
                }.also { add(it, BorderLayout.SOUTH) }
            }
        return modelDialog
    }
}