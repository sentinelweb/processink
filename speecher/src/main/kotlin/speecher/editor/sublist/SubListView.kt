package speecher.editor.sublist

import org.koin.core.context.startKoin
import speecher.di.Modules
import speecher.domain.Subtitles
import speecher.ui.listener.TextAreaListener
import speecher.ui.util.backgroundColor
import speecher.ui.util.style
import speecher.util.format.TimeFormatter
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.BevelBorder

fun main() {
    startKoin { modules(Modules.allModules) }
    SubListPresenter().apply {
        showWindow()
        testList(this)
        listener = object : SubListContract.Listener {

            var selected: Int? = null

            override fun onItemSelected(sub: Subtitles.Subtitle, index: Int) {
                if (selected == index) {
                    selected = null
                } else {
                    selected = index
                }
                setSelected(index)
            }
        }
    }
}

private fun testList(subListPresenter: SubListContract.External) {
    subListPresenter.setList(
        Subtitles(
            (0..300).mapIndexed { i, e ->
                Subtitles.Subtitle(e.toFloat(), e.toFloat() + 1, listOf("subtitle $i"))
            })
    )
}

class SubListView(
    private val presenter: SubListContract.Presenter,
    private val timeFormatter: TimeFormatter
) : SubListContract.View {

    private lateinit var frame: JFrame
    lateinit var listPanel: JPanel

    private val bgColor: Color = backgroundColor

    override fun showWindow(x: Int, y: Int) {
        SwingUtilities.invokeLater {
            if (!this::frame.isInitialized) {
                frame = JFrame("Subtitle list")
                frame.defaultCloseOperation = JFrame.HIDE_ON_CLOSE

                frame.add(SubListPanel())
                frame.setLocation(x, y)
                // Display the window.
                frame.pack()
            }
            frame.isVisible = true
        }
    }

    override fun setSelected(index: Int) {
        if (index < listPanel.components.size) {
            (listPanel.components[index] as SubListItem).selected = true
        }
    }

    override fun clearSelected(index: Int) {
        if (index < listPanel.components.size) {
            (listPanel.components[index] as SubListItem).selected = false
        }
    }

    override fun buildList(subs: Map<Int, Subtitles.Subtitle>) {
        SwingUtilities.invokeLater {
            listPanel.removeAll()
            subs.keys.forEach { key ->
                subs[key]?.let { listPanel.add(SubListItem(key, it)) }
            }
            if (subs.size < 5) {
                (subs.size..5).forEach { listPanel.add(JPanel()) }
            }
            frame.rootPane.updateUI()
        }
    }

    override fun setTitle(title: String) {
        SwingUtilities.invokeLater {
            frame.title = title
        }
    }


    inner class SubListPanel : JPanel() {
        init {
            preferredSize = Dimension(300, 650)
            layout = BorderLayout()
            listPanel = JPanel().apply {
                layout = GridLayout(-1, 1)
                background = bgColor
            }
            JScrollPane(listPanel).apply {
                layout = ScrollPaneLayout()
                verticalScrollBar.unitIncrement = 32
                background = bgColor
            }.also { add(it, BorderLayout.CENTER) }
            JTextField().style().apply {
                toolTipText = "search"
                preferredSize = Dimension(80, 30)
                background = bgColor
                document.addDocumentListener(TextAreaListener {
                    presenter.searchText(it)
                })
            }.also { add(it, BorderLayout.NORTH) }
        }
    }

    inner class SubListItem constructor(
        private val index: Int,
        item: Subtitles.Subtitle
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
            add(JLabel("${timeFormatter.formatTime(item.fromSec)} -> ${timeFormatter.formatTime(item.toSec)}s").style())
            item.text.forEach { add(JLabel(it).style()) }
            border = BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.BLACK, Color.GRAY)
            background = if (selected) colorSelected else colorNormal

            addMouseListener(object : MouseAdapter() {

                override fun mousePressed(e: MouseEvent) {
                    mouseDown = true
                    setBackground()
                    presenter.onItemClicked(index)
                }

                override fun mouseReleased(e: MouseEvent) {
                    mouseDown = false
                    setBackground()
                }
            })
        }

        private fun setBackground() {
            background = if (mouseDown) colorClick else if (selected) colorSelected else colorNormal
        }
    }
}