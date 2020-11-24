package speecher.editor.sublist

import org.koin.core.context.startKoin
import speecher.di.Modules
import speecher.domain.Subtitles
import speecher.editor.util.backgroundColor
import speecher.editor.util.style
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

    override fun setTitle(title: String) {
        SwingUtilities.invokeLater {
            frame.title = title
        }
    }

    // todo a model should be passed here
    override fun buildList(subs: Subtitles) {
        SwingUtilities.invokeLater {
            listPanel.removeAll()
            subs.timedTexts.forEachIndexed { index, item ->
                listPanel.add(SubListItem(index, item))
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