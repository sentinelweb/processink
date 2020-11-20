package speecher.generator.ui

import speecher.editor.util.setup
import speecher.editor.util.titledBorder
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*

class SpeechView constructor(
    private val presenter: SpeechContract.Presenter
) : SpeechContract.View {

    private lateinit var frame: JFrame
    lateinit var speechPanel: JPanel

    override fun showWindow() {
        SwingUtilities.invokeLater {
            if (!this::frame.isInitialized) {
                frame = JFrame("Speech editor")
                frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
                speechPanel = SpeechPanel()
                frame.add(speechPanel)
                // Display the window.
                frame.pack()
            }
            frame.isVisible = true
        }
    }

    inner class SpeechPanel : JPanel() {
        val playButton: JButton
        val pauseButton: JButton

        init {
            preferredSize = Dimension(1024, 280)
            layout = BorderLayout()

            add(JPanel().apply {
                layout = GridLayout(-1, 1)
                titledBorder("PLAY CONTROLS")
                // east panel - shader
                add(JPanel().apply {
                    preferredSize = Dimension(900, 80)
                    layout = BoxLayout(this, BoxLayout.LINE_AXIS)

                    playButton = JButton(">")
                        .setup { presenter.play() }
                        .let { add(it); it }
                    pauseButton = JButton("||")
                        .setup { presenter.pause() }
                        .let { add(it); it }
                    //.let { it.isVisible = false; it }
                    add(JButton("< LastWord").setup { presenter.moveCursorForward() })
                    add(JButton("NextWord >").setup { presenter.moveCursorBack() })

                })
            })
        }
    }
}