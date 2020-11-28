package speecher.generator.ui

import org.drjekyll.fontchooser.FontDialog
import speecher.domain.Sentence
import speecher.domain.Subtitles
import speecher.generator.ui.SpeechContract.CursorPosition.*
import speecher.generator.ui.SpeechContract.SortOrder.*
import speecher.ui.layout.wrap.WrapLayout
import speecher.ui.listener.TextAreaListener
import speecher.ui.util.*
import speecher.util.format.TimeFormatter
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import javax.swing.*
import javax.swing.border.BevelBorder
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class SpeechView constructor(
    private val presenter: SpeechContract.Presenter,
    private val timeFormatter: TimeFormatter,
    private val subChipListener: SpeechContract.SubListener,
    private val wordChipListener: SpeechContract.WordListener
) : SpeechContract.View {

    private lateinit var frame: JFrame
    lateinit var speechPanel: SpeechPanel
    private val bgColor: Color = backgroundColor

    override fun showWindow() {
        SwingUtilities.invokeLater {
            if (!this::frame.isInitialized) {
                frame = JFrame("Speech editor")
                frame.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
                frame.addWindowListener(object : WindowAdapter() {
                    override fun windowClosing(e: WindowEvent) {
                        presenter.shutdown()
                    }
                })
                speechPanel = SpeechPanel()
                frame.add(speechPanel)
                addMenu(frame)
                frame.pack()
            }
            // Display the window.
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

    override fun setSentenceId(currentSentenceId: String?) {
        speechPanel.sentenceIdText.text = currentSentenceId
    }

    override fun setStatus(status: String) {
        speechPanel.statusBar.text = status
    }

    override fun clearStatus() {
        speechPanel.statusBar.text = ""
    }

    override fun showPreviewing(value: Boolean) {
        speechPanel.previewButton.isSelected = value
    }

    inner class SpeechPanel : JPanel() {

        val sentencePanel: JPanel
        val subsPanel: JPanel
        val playCtnr: JPanel
        val searchText: JTextField
        val volumeSlider: JSlider
        val latencySlider: JSlider
        val sortNatural: JToggleButton
        val sortAZ: JToggleButton
        val sortZA: JToggleButton
        val sentenceIdText: JTextField
        val statusBar: JLabel
        val previewButton: JToggleButton

        private val playButton: JButton
        private val pauseButton: JButton
        private val loopButton: JToggleButton
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
                        JButton().icon("baseline_first_page_black_18.png")
                            .style()
                            .setup { presenter.moveCursor(START) }
                            .also { add(it) }

                        JButton().icon("baseline_arrow_back_black_18.png")
                            .style()
                            .setup { presenter.moveCursor(LAST) }
                            .also { add(it) }

                        JButton().icon("baseline_arrow_forward_black_18.png")
                            .style()
                            .setup { presenter.moveCursor(NEXT) }
                            .also { add(it) }

                        JButton().icon("baseline_last_page_black_18.png")
                            .style()
                            .setup { presenter.moveCursor(END) }
                            .also { add(it) }

                        JButton().icon("baseline_sort_black_18.png")
                            .style()
                            .setup { presenter.backSpace() }
                            .also { add(it) }

                        JButton().icon("baseline_cancel_presentation_black_18.png")
                            .style()
                            .setup { presenter.deleteWord() }.also { add(it) }

                        JButton().icon("baseline_fiber_new_black_18.png")
                            .style()
                            .setup { presenter.newSentence() }
                            .also { add(it) }

                        JButton().icon("baseline_save_alt_black_18.png")
                            .style()
                            .setup { presenter.commitSentence() }
                            .also { add(it) }

                        previewButton = JToggleButton("X").icon("baseline_preview_black_18.png")
                            .style()
                            .setup { presenter.stopPreview() }
                            .also { add(it) }

                        sentenceIdText = JTextField("")
                            .style()
                            .apply {
                                toolTipText = "sentence ID"
                                preferredSize = Dimension(50, 20)
                                background = bgColor
                                document.addDocumentListener(object : DocumentListener {
                                    override fun insertUpdate(e: DocumentEvent) {
                                        presenter.sentenceId(e.document.getText(0, e.document.length))
                                    }

                                    override fun removeUpdate(e: DocumentEvent) {
                                        presenter.sentenceId(e.document.getText(0, e.document.length))
                                    }

                                    override fun changedUpdate(e: DocumentEvent) {
                                        presenter.sentenceId(e.document.getText(0, e.document.length))
                                    }

                                })
                            }
                            .also { add(it) }
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
                                playButton = JButton()
                                    .icon("baseline_play_arrow_black_18.png")
                                    .style()
                                    .setup { presenter.play() }
                                    .also { add(it, PLAY_BUT) }

                                pauseButton = JButton()
                                    .icon("baseline_pause_black_18.png")
                                    .style()
                                    .setup { presenter.pause() }
                                    .also { add(it, PAUSE_BUT) }

                            }.also { add(it) }
                            loopButton = JToggleButton()
                                .icon("baseline_loop_black_18.png")
                                .style()
                                .setup { ae -> presenter.loop(isSelected(ae)) }
                                .also { add(it) }

                            fontButton = JButton()
                                .icon("baseline_font_download_black_18.png")
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

                            fontColorButton = JButton()
                                .icon("baseline_format_color_fill_black_18.png")
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

                        volumeSlider = JSlider(0, VOL_SCALE.toInt(), 100)
                            .apply {
                                preferredSize = Dimension(20, 200)
                                background = bgColor
                            }
                            .setup(null, -1, -1, false) {
                                val source = it.source as JSlider
//                                if (source.getValueIsAdjusting()) {
                                presenter.volume = source.value / VOL_SCALE

                            }.let { add(it); it.orientation = JSlider.VERTICAL; it }

                        latencySlider = JSlider(0, 100, 100)
                            .apply {
                                preferredSize = Dimension(20, 200)
                                background = bgColor
                                majorTickSpacing = 10
                                paintTicks = true
                                value = presenter.playEventLatency?.let { (it * PLAT_SCALE).toInt() } ?: 0
                            }
                            .setup(null, -1, -1, false) {
                                val source = it.source as JSlider
                                if (source.getValueIsAdjusting()) {
                                    presenter.playEventLatency = source.value / PLAT_SCALE
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
                    preferredSize = Dimension(1024, 36)
                    layout = BoxLayout(this, BoxLayout.LINE_AXIS)
                    background = bgColor
                    searchText = JTextField("")
                        .style()
                        .apply {
                            toolTipText = "search"
                            preferredSize = Dimension(80, 24)
                            background = bgColor
                            document.addDocumentListener(TextAreaListener {
                                presenter.searchText(it)
                            })
                        }
                        .also { add(it) }
                    JPanel().apply {
                        background = bgColor
                        sortNatural = JToggleButton()
                            .icon("baseline_sort_black_18.png")
                            .setup { deselectOthersAction(it);presenter.sortOrder(NATURAL) }
                            .style()
                            .also { add(it) }
                        sortAZ = JToggleButton()
                            .icon("baseline_sort_by_alpha_black_18.png")
                            .setup { deselectOthersAction(it);presenter.sortOrder(A_Z) }
                            .style()
                            .also { add(it) }
                        sortZA = JToggleButton()
                            .icon("baseline_sort_by_alpha_rev_black_18.png")
                            .setup { deselectOthersAction(it);presenter.sortOrder(Z_A) }
                            .style()
                            .also { add(it) }

                        JButton()
                            .icon("baseline_refresh_black_18.png")
                            .setup { presenter.reloadWords() }
                            .style()
                            .also { add(it) }
                    }
                        .also { add(it) }
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

                statusBar = JLabel().style().apply {
                    preferredSize = Dimension(1024, 30)
                    background = bgColor
                    border = CompoundBorder(
                        BevelBorder(BevelBorder.RAISED, Color.decode("#cccccc"), Color.decode("#888888")),
                        EmptyBorder(5, 5, 5, 5)
                    )
                    foreground = Color.RED
                }.also { add(it, BorderLayout.SOUTH) }

            }.also { add(it) }
        }
    }

    private fun addMenu(mainFrame: JFrame) {
        //create a menu bar
        val menuBar = JMenuBar()

        //create menus
        val fileMenu = JMenu("File")
        fileMenu.mnemonic = KeyEvent.VK_F
        //fileMenu.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.META_DOWN_MASK)
        val editMenu = JMenu("Edit")
        editMenu.mnemonic = KeyEvent.VK_E
        val viewMenu = JMenu("View")
        editMenu.mnemonic = KeyEvent.VK_V
        //fileMenu.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.META_DOWN_MASK)
        //create menu items
        val openMovieMenuItem = JMenuItem("Open Movie")
        openMovieMenuItem.mnemonic = KeyEvent.VK_M
        openMovieMenuItem.actionCommand = "Open"
        openMovieMenuItem.icon("baseline_movie_black_18.png")
        openMovieMenuItem.addActionListener { presenter.openMovie() }

        val openReadSrtMenuItem = JMenuItem("Open SRT Words")
        openReadSrtMenuItem.mnemonic = KeyEvent.VK_W
        openReadSrtMenuItem.actionCommand = "Open"
        openReadSrtMenuItem.icon("baseline_subtitles_black_18.png")
        openReadSrtMenuItem.addActionListener { presenter.openWords() }

        val openSentencesMenuItem = JMenuItem("Open Sentences")
        openSentencesMenuItem.mnemonic = KeyEvent.VK_O
        openSentencesMenuItem.actionCommand = "Open Setences"
        openSentencesMenuItem.icon("baseline_text_snippet_black_18.png")
        openSentencesMenuItem.addActionListener { presenter.openSentences() }

        val saveSentencesMenuItem = JMenuItem("Save Sentences")
        saveSentencesMenuItem.mnemonic = KeyEvent.VK_S
        saveSentencesMenuItem.actionCommand = "Save"
        saveSentencesMenuItem.icon("baseline_save_black_18.png")
        saveSentencesMenuItem.addActionListener { presenter.saveSentences(false) }

        val saveSentencesAsMenuItem = JMenuItem("Save Sentences As ...")
        saveSentencesAsMenuItem.mnemonic = KeyEvent.VK_A
        saveSentencesAsMenuItem.actionCommand = "Save As"
        saveSentencesAsMenuItem.icon("content-save-as_black_18.png")
        saveSentencesAsMenuItem.addActionListener { presenter.saveSentences(true) }

        val exitMenuItem = JMenuItem("Exit")
        exitMenuItem.mnemonic = KeyEvent.VK_X
        exitMenuItem.actionCommand = "Exit"
        exitMenuItem.icon("baseline_exit_to_app_black_18.png")
        exitMenuItem.addActionListener { presenter.shutdown() }

        val cutMenuItem = JMenuItem("Cut")
        cutMenuItem.mnemonic = KeyEvent.VK_X
        cutMenuItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK)
        cutMenuItem.actionCommand = "Cut"
        cutMenuItem.icon("baseline_content_cut_black_18.png")
        cutMenuItem.addActionListener { presenter.cut() }

        val copyMenuItem = JMenuItem("Copy")
        copyMenuItem.mnemonic = KeyEvent.VK_C
        copyMenuItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK)
        copyMenuItem.actionCommand = "Copy"
        copyMenuItem.icon("baseline_content_copy_black_18.png")
        copyMenuItem.addActionListener { presenter.copy() }

        val pasteMenuItem = JMenuItem("Paste")
        pasteMenuItem.mnemonic = KeyEvent.VK_V
        pasteMenuItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK)
        pasteMenuItem.actionCommand = "Paste"
        pasteMenuItem.icon("baseline_content_paste_black_18.png")
        pasteMenuItem.addActionListener { presenter.paste() }

        val showReadSrtMenuItem = JMenuItem("Show sentences")
        showReadSrtMenuItem.mnemonic = KeyEvent.VK_R
        showReadSrtMenuItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.META_DOWN_MASK)
        showReadSrtMenuItem.icon("baseline_list_black_18.png")
        showReadSrtMenuItem.addActionListener { presenter.showSentences() }

        //add menu items to menus
        fileMenu.add(openMovieMenuItem.style())
        fileMenu.addSeparator()
        fileMenu.add(openReadSrtMenuItem.style())
        fileMenu.addSeparator()
        fileMenu.add(openSentencesMenuItem.style())
        fileMenu.add(saveSentencesMenuItem.style())
        fileMenu.add(saveSentencesAsMenuItem.style())
        fileMenu.addSeparator()
        fileMenu.add(exitMenuItem.style())

        editMenu.add(cutMenuItem.style())
        editMenu.add(copyMenuItem.style())
        editMenu.add(pasteMenuItem.style())

        viewMenu.add(showReadSrtMenuItem.style())

        //add menu to menubar
        menuBar.add(fileMenu.style())
        menuBar.add(editMenu.style())
        menuBar.add(viewMenu.style())

        //add menubar to the frame
        mainFrame.setJMenuBar(menuBar)
    }

    override fun showOpenDialog(title: String, currentDir: File?, chosen: (f: File) -> Unit) {
        JFileChooser().apply {
            isMultiSelectionEnabled = false
            fileSelectionMode = JFileChooser.FILES_ONLY
            currentDir?.let { currentDirectory = it }
            val result = showOpenDialog(frame)
            if (result == JFileChooser.APPROVE_OPTION) {
                chosen(selectedFile)
            }
        }
    }

    override fun showSaveDialog(title: String, currentDir: File?, chosen: (File) -> Unit) {
        JFileChooser().apply {
            isMultiSelectionEnabled = false
            fileSelectionMode = JFileChooser.FILES_ONLY
            currentDir?.let { if (it.isDirectory) currentDirectory = it else selectedFile = it }
            val result = showSaveDialog(frame)
            if (result == JFileChooser.APPROVE_OPTION) {
                chosen(selectedFile)
            }
        }
    }

    override fun clearFocus() {
        speechPanel.sentenceIdText.requestFocusInWindow()
        speechPanel.searchText.requestFocusInWindow()
    }

    override fun updateMultiSelection(keys: MutableSet<Int>) {
        speechPanel.sentencePanel.components.forEachIndexed { i, wv ->
            (wv as WordChipView).selected = keys.contains(i)
        }
    }

    override fun selectWord(index: Int, selected: Boolean) {
        speechPanel.sentencePanel.components[index].let {
            (it as WordChipView).interfaceVisible = selected
        }
    }

    override fun restoreState(
        vol: Float,
        playEventLatency: Float?,
        searchText: String?,
        sortOrder: SpeechContract.SortOrder,
        currentSentenceId: String?
    ) {
        speechPanel.volumeSlider.value = (vol * VOL_SCALE).toInt()
        playEventLatency?.apply { speechPanel.latencySlider.value = (this * PLAT_SCALE).toInt() }
        speechPanel.searchText.text = searchText
        when (sortOrder) {
            NATURAL -> speechPanel.sortNatural.isSelected = true
            A_Z -> speechPanel.sortAZ.isSelected = true
            Z_A -> speechPanel.sortZA.isSelected = true
        }
        speechPanel.sentenceIdText.text = currentSentenceId
    }

    companion object {
        private const val PLAY_BUT = "playBut"
        private const val PAUSE_BUT = "pauseBut"
        private const val VOL_SCALE = 100f
        private const val PLAT_SCALE = 1000f
    }
}