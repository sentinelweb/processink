package speecher.editor.sublist

import org.koin.core.context.startKoin
import speecher.di.Modules
import speecher.domain.Subtitles
import speecher.util.format.TimeFormatter
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.BevelBorder

fun main() {
    startKoin {
        modules(Modules.allModules)
    }
    //val subListView = SubListView(TimeFormatter())
    testList(SubListPresenter())
    //subListView.showWindow()
    //SwingUtilities.invokeLater {
    //testList(subListView)
    // }
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

    override fun showWindow() {
        SwingUtilities.invokeLater {
            if (!this::frame.isInitialized) {
                frame = JFrame("Subtitle list")
                frame.defaultCloseOperation = JFrame.HIDE_ON_CLOSE

                frame.add(SubListPanel())

                // Display the window.
                frame.pack()
            }
            frame.isVisible = true
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
            preferredSize = Dimension(200, 650)
            layout = GridLayout(1, 1)
            listPanel = JPanel().apply {
                layout = GridLayout(-1, 1)
                //add(JLabel("init"))
            }
            add(
                JScrollPane(listPanel).apply {
                    layout = ScrollPaneLayout()
                }
            )
        }
    }

//    inner class SubListPanel : JScrollPane() {
//        init {
//            preferredSize = Dimension(300, 450)
//            layout = ScrollPaneLayout()
//            listPanel = JPanel().apply {
//                layout = GridLayout(-1, 1)
//            }.also { add(it) }
//        }
//    }

    inner class SubListItem constructor(
        private val index: Int,
        item: Subtitles.Subtitle
    ) : JPanel() {
        init {
            layout = GridLayout(-1, 1)

            add(JLabel("${timeFormatter.formatTime(item.fromSec)} -> ${timeFormatter.formatTime(item.toSec)}s"))
            add(JLabel(item.text.joinToString("\n")))
            border = BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.BLACK, Color.GRAY)

            addMouseListener(object : MouseAdapter() {
                private var background: Color? = null

                override fun mousePressed(e: MouseEvent?) {
                    background = getBackground()
                    setBackground(Color.LIGHT_GRAY)
                    repaint()
                    presenter.onItemClicked(index)
                }

                override fun mouseReleased(e: MouseEvent?) {
                    setBackground(background)
                }
            })
        }
    }
}