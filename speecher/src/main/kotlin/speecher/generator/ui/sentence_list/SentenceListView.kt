package speecher.generator.ui.sentence_list

import org.koin.core.context.startKoin
import speecher.di.Modules
import speecher.domain.Sentence
import speecher.domain.Subtitles
import speecher.ui.image.Image
import speecher.ui.util.backgroundColor
import speecher.ui.util.icon
import speecher.ui.util.style
import speecher.util.format.TimeFormatter
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.BevelBorder

fun main() {
    startKoin { modules(Modules.allModules) }
    SentenceListPresenter().apply {
        showWindow()
        testList(this)
        listener = object : SentenceListContract.Listener {

            var selected: String? = null

            override fun onItemSelected(key: String, sentence: Sentence) {
                if (selected == key) {
                    selected = null
                } else {
                    selected = key
                }
                setSelected(key)
            }
        }
    }
}

private fun testList(sentenceListPresenter: SentenceListContract.External) {
    sentenceListPresenter.setList(
        (0..10).map { e ->
            "key $e" to Sentence((100..130).mapIndexed { i, e ->
                Sentence.Word(Subtitles.Subtitle(i.toFloat(), (i + 1).toFloat(), listOf("sub $e")))
            })
        }.toMap()
    )
}

class SentenceListView(
    private val presenter: SentenceListContract.Presenter,
    private val timeFormatter: TimeFormatter
) : SentenceListContract.View {

    private lateinit var frame: JFrame
    lateinit var listPanel: JPanel

    private val bgColor: Color = backgroundColor

    private var keyToIndexMap: MutableMap<String, Int> = mutableMapOf()

    override fun showWindow() {
        SwingUtilities.invokeLater {
            if (!this::frame.isInitialized) {
                frame = JFrame("Sentences")
                frame.defaultCloseOperation = JFrame.HIDE_ON_CLOSE

                frame.add(SubListPanel())
                frame.setLocation(1500, 0)
                // Display the window.
                frame.pack()
            }
            frame.isVisible = true
        }
    }

    override fun setSelected(key: String) {
        keyToIndexMap[key]?.let {
            (listPanel.components[it] as SentenceItem).selected = true
        }
    }

    override fun clearSelected(key: String) {
        keyToIndexMap[key]?.let {
            (listPanel.components[it] as SentenceItem).selected = false
        }
    }

    // todo a model should be passed here
    override fun buildList(sentences: Map<String, String>) {
        SwingUtilities.invokeLater {
            listPanel.removeAll()
            keyToIndexMap.clear()
            sentences.keys.forEachIndexed { i, k ->
                keyToIndexMap[k] = i
                listPanel.add(SentenceItem(k, sentences[k]!!))
            }
            if (sentences.size < 5) {
                (sentences.size..5).forEach { listPanel.add(JPanel()) }
            }
            frame.rootPane.updateUI()
        }
    }

    inner class SubListPanel : JPanel() {
        init {
            preferredSize = Dimension(300, 650)
            layout = GridLayout(1, 1)
            listPanel = JPanel().apply {
                layout = GridLayout(-1, 1)
                background = bgColor
                //add(JLabel("init"))
            }
            add(
                JScrollPane(listPanel).apply {
                    layout = ScrollPaneLayout()
                    verticalScrollBar.unitIncrement = 32
                    background = bgColor
                }
            )
        }
    }

    inner class SentenceItem constructor(
        private val key: String,
        item: String
    ) : JPanel() {

        private val colorSelected = Color.decode("#cccccc")
        private val colorNormal = bgColor
        private val colorClick = Color.decode("#aaaaaa")

        private var mouseDown = false

        var selected = false
            set(value) {
                field = value
                setBackground()
            }

        init {
            layout = GridLayout(-1, 1)
            val mouseListener = object : MouseAdapter() {
                private var menu: JPopupMenu? = null
                override fun mousePressed(e: MouseEvent) {
                    if (e.isPopupTrigger()) {
                        menu = PopUp(key).apply { show(e.component, e.x, e.y) }
                    } else {
                        mouseDown = true
                        setBackground()
                        presenter.onItemClicked(key)
                    }
                }

                override fun mouseReleased(e: MouseEvent) {
                    if (e.isPopupTrigger()) {
                        menu?.hide()
                    } else {
                        mouseDown = false
                        setBackground()
                    }
                }
            }

            JLabel(key).style().also { add(it) }
            JTextArea(item).style().apply {
                wrapStyleWord = true
                lineWrap = true
                isEditable = false
                addMouseListener(mouseListener)
            }.also { add(it) }

//            JLabel(String.format(
//                "<html><body style=\"text-align: justify;  text-justify: inter-word;\">%s</body></html>",
//                item
//            )).also { add(it) }

            border = BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.BLACK, Color.GRAY)
            background = if (selected) colorSelected else colorNormal

            addMouseListener(mouseListener)
        }

        private fun setBackground() {
            background = if (mouseDown) colorClick else if (selected) colorSelected else colorNormal
        }

        inner class PopUp(private val key: String) : JPopupMenu() {
            var anItem: JMenuItem

            init {
                anItem = JMenuItem("Delete").icon("baseline_delete_black_18.png").style()
                anItem.addActionListener { ae ->
                    presenter.onDelete(key)
                }
                add(anItem)
            }
        }
    }

    override fun showDeleteConfirm(msg: String, confirm: () -> Unit) {
        val res = JOptionPane.showConfirmDialog(
            null,
            msg,
            "Confirm delete ...",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            Image("baseline_delete_black_18.png")
        )
        if (res == JOptionPane.OK_OPTION) {
            confirm()
        }
    }
}