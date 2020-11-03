package speecher.editor.subedit

import speecher.editor.subedit.multithumbslider.MultiThumbSlider
import speecher.editor.util.titledBorder
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities

fun main() {
    SubEditView().showWindow()
}

class SubEditView : SubEditContract.View {

    override fun showWindow() {
        SwingUtilities.invokeLater {
            val frame = JFrame("Transport")
            frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

            val controlPanel = SubEdittPanel()
            frame.add(controlPanel)
            // Display the window.
            frame.pack()
            frame.isVisible = true
        }
    }

    inner class SubEdittPanel : JPanel() {
        init {
            add(JPanel().apply {
                layout = GridLayout(-1, 1)
                titledBorder("TEST")

                MultiThumbSlider<Float>(floatArrayOf(0.1f, 0.2f, 0.3f, 0.4f), arrayOf(1f, 2f, 3f, 4f)).let {
                    it.preferredSize = Dimension(1000, 60)
                    it.addChangeListener { changeEvent ->
                        val source = changeEvent.source as MultiThumbSlider<*>
                        println(source.thumbPositions.joinToString { it.toString() })
                    }
                    add(it)
                }

            })
        }
    }
}