package speecher.editor.subedit

import org.koin.core.context.startKoin
import speecher.di.Modules
import speecher.domain.Subtitles
import speecher.editor.util.deselectOthers
import speecher.editor.util.setup
import speecher.ui.layout.wrap.WrapLayout
import speecher.ui.multithumbslider.MultiThumbSlider
import speecher.util.format.TimeFormatter
import java.awt.*
import javax.swing.*

fun main() {
    startKoin { modules(Modules.allModules) }
    SubEditPresenter().apply {
        showWindow()
        SwingUtilities.invokeLater {
            setReadSub(
                Subtitles.Subtitle(
                    20f, 40f,
                    listOf(
                        "Une Deux Troi Carte Cinq Six Set Huit Neuf Dix Onze",
                        "Douze Treze Qatorze Qinze Dix-sept Dix-huit Dix-neuf Vingt"
                    )
                )
            )
        }
    }
}

class SubEditView constructor(
    private val presenter: SubEditContract.Presenter,
    private val timeFormatter: TimeFormatter
) : SubEditContract.View {

    private lateinit var controlPanel: SubEdittPanel

    override fun showWindow() {
        SwingUtilities.invokeLater {
            val frame = JFrame("Edit subtitles")
            frame.defaultCloseOperation = JFrame.HIDE_ON_CLOSE

            controlPanel = SubEdittPanel()
            frame.add(controlPanel)
            frame.location = Point(200, 340)
            // Display the window.
            frame.pack()
            frame.isVisible = true
        }
    }

    inner class SubEdittPanel : JPanel() {
        val wordPanel: JPanel
        val slider: MultiThumbSlider<Float>
        val timeText: JLabel
        val startTimeText: JLabel
        val endTimeText: JLabel
        val saveButton: JButton
        val saveNextButton: JButton

        init {
            add(JPanel().apply {
                layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
                preferredSize = Dimension(1024, 160)
                wordPanel = JPanel().let {
                    it.layout = WrapLayout();
                    size = Dimension(1024, 1)
                    add(it); it
                }
                add(JPanel().apply {
                    layout = BorderLayout()
                    startTimeText = JLabel("00:00:00").let { add(it, BorderLayout.WEST); it }
                    endTimeText = JLabel("00:00:00").let { add(it, BorderLayout.EAST); it }
                })
                add(JPanel().apply {
                    layout = GridLayout(-1, 1)
                    slider = MultiThumbSlider<Float>(floatArrayOf(0.1f, 0.2f), arrayOf(1f, 2f))
                        .let {
                            // it.preferredSize = Dimension(1000, 60)
                            it.collisionPolicy = MultiThumbSlider.Collision.STOP_AGAINST
//                    it.isAutoAdding = false
                            it.addChangeListener { changeEvent ->
                                val source = changeEvent.source as MultiThumbSlider<*>
                                presenter.sliderChanged(
                                    source.selectedThumb,
                                    source.thumbPositions[source.selectedThumb]
                                )
                            }
                            add(it); it
                        }
                    timeText = JLabel("00:00:00 -> 00:00:00").let { add(it); it }
                })

                add(JPanel().apply {
                    layout = FlowLayout(FlowLayout.RIGHT)
                    saveButton = JButton("Save").setup { presenter.onSave(false) }.let { add(it); it }
                    saveNextButton = JButton("Save/Next").setup { presenter.onSave(true) }.let { add(it); it }
                })
            })
        }
    }

    override fun setLimits(fromSec: Float, toSec: Float) {
        controlPanel.startTimeText.text = timeFormatter.formatTime(fromSec)
        controlPanel.endTimeText.text = timeFormatter.formatTime(toSec)
        controlPanel.updateUI()
    }

    override fun setMarkers(markers: List<Float>) {
        controlPanel.slider.thumbPositions[0] = markers[0]
        controlPanel.slider.thumbPositions[1] = markers[1]
    }

    override fun setWordList(words: List<String>) {
        controlPanel.wordPanel.removeAll()
        words.forEachIndexed { i, word ->
            controlPanel.wordPanel.add(JToggleButton(word).setup {
                deselectOthers(it)
                presenter.wordSelected(i)
            })
        }
        controlPanel.updateUI()
    }

}