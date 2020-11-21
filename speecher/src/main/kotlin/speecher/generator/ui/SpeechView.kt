package speecher.generator.ui

import speecher.domain.Sentence
import speecher.domain.Subtitles
import speecher.editor.util.setup
import speecher.editor.util.titledBorder
import speecher.generator.ui.SpeechContract.CursorPosition.*
import speecher.generator.ui.SpeechContract.SortOrder.*
import speecher.ui.layout.wrap.WrapLayout
import speecher.util.format.TimeFormatter
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class SpeechView constructor(
    private val presenter: SpeechContract.Presenter,
    private val timeFormatter: TimeFormatter,
    private val subChipListener: SubtitleChipView.Listener

) : SpeechContract.View {

    private lateinit var frame: JFrame
    lateinit var speechPanel: SpeechPanel

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

    override fun updateSentence(sentence: List<Sentence.Item>) {

    }

    override fun updateSubList(subs: List<Subtitles.Subtitle>) {
        subs.forEachIndexed { i, sub ->
            SubtitleChipView(timeFormatter, sub, subChipListener).also {
                speechPanel.subsPanel.add(it)
            }

        }
    }

    inner class SpeechPanel : JPanel() {
        val playButton: JButton
        val pauseButton: JButton
        val sentencePanel: JPanel
        val subsPanel: JPanel
        val searchText: JTextField

        init {
            preferredSize = Dimension(1024, 768)
            layout = GridLayout(-1, 1)

            JPanel().apply {
                layout = GridLayout(-1, 1)
                titledBorder("SENTENCE")

                JPanel().apply {
                    layout = BorderLayout()

                    JPanel().apply {
                        preferredSize = Dimension(1024, 80)
                        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
                        titledBorder("CURSOR")
                        add(JButton("|<").setup { presenter.moveCursor(START) })
                        add(JButton("<<").setup { presenter.moveCursor(LAST) })
                        add(JButton(">>").setup { presenter.moveCursor(NEXT) })
                        add(JButton(">|").setup { presenter.moveCursor(END) })
                    }.also { add(it, BorderLayout.NORTH) }

                    // east panel - play control
                    JPanel().apply {
                        preferredSize = Dimension(124, 80)
                        layout = BoxLayout(this, BoxLayout.LINE_AXIS)

                        playButton = JButton(">")
                            .setup { presenter.play() }
                            .let { add(it); it }
                        pauseButton = JButton("||")
                            .setup { presenter.pause() }
                            .let { add(it); it }
                            .let { it.isVisible = false; it }
                    }.also { add(it, BorderLayout.EAST) }

                    sentencePanel = JPanel().apply {
                        preferredSize = Dimension(900, 300)
                        layout = WrapLayout()
                    }
                    sentencePanel.let {
                        JScrollPane(it).apply {
                            layout = ScrollPaneLayout()
                            verticalScrollBar.unitIncrement = 32
                        }
                    }.also { add(it, BorderLayout.CENTER) }
                }.also { add(it) }
            }.also { add(it) }

            // -----------------------------------------------------------

            JPanel().apply {
                layout = GridLayout(-1, 1)
                titledBorder("SUBTITLES")

                JPanel().apply {
                    preferredSize = Dimension(1024, 30)
                    layout = BoxLayout(this, BoxLayout.LINE_AXIS)
                    searchText = JTextField("")
                        .apply {
                            toolTipText = "search"
                            preferredSize = Dimension(80, 30)
                            document.addDocumentListener(object : DocumentListener {
                                override fun insertUpdate(e: DocumentEvent) {
                                    presenter.searchText(e.document.getText(0, e.document.length))
                                }

                                override fun removeUpdate(e: DocumentEvent) {
                                    presenter.searchText(e.document.getText(0, e.document.length))
                                }

                                override fun changedUpdate(e: DocumentEvent) {
                                    presenter.searchText(e.document.getText(0, e.document.length))
                                }

                            })
                        }
                        .also { add(it) }
                    add(JButton("Natural").setup { presenter.sortOrder(NATURAL) })
                    add(JButton("A-Z").setup { presenter.sortOrder(A_Z) })
                    add(JButton("Z-A").setup { presenter.sortOrder(Z_A) })
                    add(JButton("...").setup { presenter.openSubs() })
                }.also { add(it, BorderLayout.NORTH) }

                subsPanel = JPanel().apply {
                    layout = WrapLayout()
                }
                subsPanel.let {
                    JScrollPane(it).apply {
                        layout = ScrollPaneLayout()
                        verticalScrollBar.unitIncrement = 32
                    }
                }.also { add(it, BorderLayout.CENTER) }
            }.also { add(it) }
        }
    }

}