package speecher.generator.ui

import org.drjekyll.fontchooser.FontDialog
import speecher.domain.Sentence
import speecher.domain.Subtitles
import speecher.editor.util.*
import speecher.generator.ui.SpeechContract.CursorPosition.*
import speecher.generator.ui.SpeechContract.SortOrder.*
import speecher.ui.layout.wrap.WrapLayout
import speecher.util.format.TimeFormatter
import java.awt.*
import java.io.File
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class SpeechView constructor(
    private val presenter: SpeechContract.Presenter,
    private val timeFormatter: TimeFormatter,
    private val subChipListener: SubtitleChipView.Listener,
    private val wordChipListener: WordChipView.Listener

) : SpeechContract.View {

    private lateinit var frame: JFrame
    lateinit var speechPanel: SpeechPanel
    private val bgColor: Color = backgroundColor

    override fun showWindow() {
        SwingUtilities.invokeLater {
            if (!this::frame.isInitialized) {
                frame = JFrame("Speech editor")
                frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
                speechPanel = SpeechPanel()
                //speechPanel.updateUI()
                frame.add(speechPanel)
                // Display the window.
                frame.pack()
            }
            frame.isVisible = true
            presenter.initView()
        }
    }

    override fun updateSentence(sentence: List<Sentence.Word>) {
        speechPanel.sentencePanel.removeAll()
        sentence.forEachIndexed { i, word ->
            WordChipView(timeFormatter, word, i, wordChipListener).also {
                if (word == SpeechPresenter.CURSOR) {
                    it.background = Color.LIGHT_GRAY
                    it.isEnabled = false
                }
                speechPanel.sentencePanel.add(it)
            }
        }
        speechPanel.sentencePanel.updateUI()
    }

    override fun setPlaying(isPlaying: Boolean) {
        (speechPanel.playCtnr.layout as CardLayout)
            .show(speechPanel.playCtnr, if (!isPlaying) PLAY_BUT else PAUSE_BUT)
    }

    override fun updateSubList(subs: List<Subtitles.Subtitle>) {
        speechPanel.subsPanel.removeAll()
        subs.forEachIndexed { i, sub ->
            SubtitleChipView(timeFormatter, sub, subChipListener).also {
                speechPanel.subsPanel.add(it)
            }
        }
        speechPanel.subsPanel.updateUI()
    }

    inner class SpeechPanel : JPanel() {

        val sentencePanel: JPanel
        val subsPanel: JPanel
        val playCtnr: JPanel

        private val playButton: JButton
        private val pauseButton: JButton
        private val loopButton: JToggleButton
        private val searchText: JTextField
        private val volumeSlider: JSlider
        private val latencySlider: JSlider
        private val fontButton: JButton
        private val fontColorButton: JButton

        init {
            preferredSize = Dimension(1500, 768)
            layout = GridLayout(-1, 1)
            background = bgColor
            JPanel().apply {
                layout = GridLayout(-1, 1)
                titledBorder("SENTENCE")
                background = bgColor
                JPanel().apply {
                    layout = BorderLayout()
                    background = bgColor
                    // north panel - play control
                    JPanel().apply {
                        //preferredSize = Dimension(1024, 40)
                        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
                        background = bgColor
                        add(JButton("|<").style().setup { presenter.moveCursor(START) })
                        add(JButton("<<").style().setup { presenter.moveCursor(LAST) })
                        add(JButton(">>").style().setup { presenter.moveCursor(NEXT) })
                        add(JButton(">|").style().setup { presenter.moveCursor(END) })
                        add(JButton("DEL").style().setup { presenter.deleteWord() })
                    }.wrapWithLabel("Cursor")
                        .also { add(it, BorderLayout.NORTH) }

                    // east panel - play control
                    JPanel().apply {
                        titledBorder("Control")
                        preferredSize = Dimension(180, 80)
                        layout = GridLayout(1, -1)
                        background = bgColor
                        JPanel().apply {
                            layout = GridLayout(-1, 1)
                            preferredSize = Dimension(50, 100)
                            background = bgColor
                            playCtnr = JPanel().apply {
                                layout = CardLayout()
                                background = bgColor
                                playButton = JButton(">")
                                    .style()
                                    .setup { presenter.play() }
                                    .also { add(it, PLAY_BUT) }

                                pauseButton = JButton("||")
                                    .style()
                                    .setup { presenter.pause() }
                                    .also { add(it, PAUSE_BUT) }

                            }.also { add(it) }
                            loopButton = JToggleButton("<->")
                                .style()
                                .setup { ae -> presenter.loop(isSelected(ae)) }
                                .also { add(it) }

                            fontButton = JButton("Font")
                                .style()
                                .apply { preferredSize = Dimension(40, 40) }.setup { ae ->
                                    val dialog = FontDialog(null as Frame?, "Font Dialog Example", true)
                                    dialog.selectedFont = presenter.selectedFont
                                    dialog.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
                                    dialog.isVisible = true
                                    if (!dialog.isCancelSelected) {
                                        System.out.printf("Selected font is: %s%n", dialog.selectedFont)
                                        presenter.selectedFont = dialog.selectedFont
                                    }
                                }
                                .also { add(it) }

                            fontColorButton = JButton("FC")
                                .style()
                                .apply { preferredSize = Dimension(40, 40) }
                                .setup { ae ->
                                    val color =
                                        JColorChooser.showDialog(this, "Font Color", presenter.selectedFontColor)
                                    color?.let {
                                        @Suppress("LABEL_NAME_CLASH")
                                        this@apply.background = it
                                        presenter.selectedFontColor = it
                                    }
                                }
                                .also { add(it) }

                        }.also { add(it) }

                        volumeSlider = JSlider(0, 100, 100)
                            .apply {
                                preferredSize = Dimension(20, 200)
                                background = bgColor
                            }
                            .setup(null, -1, -1, false) {
                                val source = it.source as JSlider
//                                if (source.getValueIsAdjusting()) {
                                presenter.volume = source.value / 100f

                            }.let { add(it); it.orientation = JSlider.VERTICAL; it }

                        latencySlider = JSlider(0, 100, 100)
                            .apply {
                                preferredSize = Dimension(20, 200)
                                background = bgColor
                                majorTickSpacing = 10
                                paintTicks = true
                                value = presenter.playEventLatency?.let { (it * 1000).toInt() } ?: 0
                            }
                            .setup(null, -1, -1, false) {
                                val source = it.source as JSlider
                                if (source.getValueIsAdjusting()) {
                                    presenter.playEventLatency = source.value / 1000f
                                }

                            }.let { add(it); it.orientation = JSlider.VERTICAL; it }
                    }.also { add(it, BorderLayout.EAST) }

                    // center panel - sentence
                    sentencePanel = JPanel().apply {
                        layout = WrapLayout()
                        background = bgColor
                    }
                    sentencePanel.let {
                        JScrollPane(it).apply {
                            layout = ScrollPaneLayout()
                            background = bgColor
                            verticalScrollBar.unitIncrement = 32
                        }
                    }.also { add(it, BorderLayout.CENTER) }

                }.also { add(it) }
            }.also { add(it) }

            // -----------------------------------------------------------

            JPanel().apply {
                layout = BorderLayout()
                titledBorder("SUBTITLES")

                JPanel().apply {
                    preferredSize = Dimension(1024, 30)
                    layout = BoxLayout(this, BoxLayout.LINE_AXIS)
                    background = bgColor
                    searchText = JTextField("")
                        .style()
                        .apply {
                            toolTipText = "search"
                            preferredSize = Dimension(80, 30)
                            background = bgColor
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
                    add(JButton("Natural").setup { presenter.sortOrder(NATURAL) }.style())
                    add(JButton("A-Z").setup { presenter.sortOrder(A_Z) }.style())
                    add(JButton("Z-A").setup { presenter.sortOrder(Z_A) }.style())
                    add(JButton("...").setup { presenter.openSubs() }.style())
                }.also { add(it, BorderLayout.NORTH) }

                subsPanel = JPanel().apply {
                    layout = WrapLayout()
                    background = bgColor
                }
                subsPanel.let {
                    JScrollPane(it).apply {
                        layout = ScrollPaneLayout()
                        background = bgColor
                        verticalScrollBar.unitIncrement = 32
                    }
                }.also { add(it, BorderLayout.CENTER) }
            }.also { add(it) }
        }
    }

    override fun showOpenDialog(title: String, currentDir: File?) {
        JFileChooser().apply {
            isMultiSelectionEnabled = false
            fileSelectionMode = JFileChooser.FILES_ONLY
            currentDir?.let { currentDirectory = it }
            val result = showOpenDialog(frame)
            if (result == JFileChooser.APPROVE_OPTION) {
                presenter.setSrtFile(selectedFile)
            }
        }
    }

    override fun selectWord(index: Int, selected: Boolean) {
        speechPanel.sentencePanel.components[index].let {
            (it as WordChipView).interfaceVisible = selected
        }
    }

    companion object {
        private const val PLAY_BUT = "playBut"
        private const val PAUSE_BUT = "pauseBut"
    }
}