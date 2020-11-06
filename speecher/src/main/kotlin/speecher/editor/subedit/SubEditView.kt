package speecher.editor.subedit

import org.koin.core.context.startKoin
import speecher.di.Modules
import speecher.domain.Subtitles
import speecher.editor.util.deselectOthers
import speecher.editor.util.deselectOthersAction
import speecher.editor.util.setup
import speecher.ui.layout.wrap.WrapLayout
import speecher.ui.multithumbslider.MultiThumbSlider
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
    private val presenter: SubEditContract.Presenter
) : SubEditContract.View {

    private lateinit var controlPanel: SubEditPanel

    override fun showWindow() {
        SwingUtilities.invokeLater {
            val frame = JFrame("Edit subtitles")
            frame.defaultCloseOperation = JFrame.HIDE_ON_CLOSE

            controlPanel = SubEditPanel()
            frame.add(controlPanel)
            frame.location = Point(200, 340)
            // Display the window.
            frame.pack()
            frame.isVisible = true
        }
    }

    inner class SubEditPanel : JPanel() {
        val wordPanel: JPanel
        val slider: MultiThumbSlider<Float>
        val timeText: JLabel
        val startLimit: TimeLimitPanel
        val endLimit: TimeLimitPanel
        val saveButton: JButton
        val saveNextButton: JButton

        init {
            add(JPanel().apply {
                layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
                preferredSize = Dimension(1024, 200)
                wordPanel = JPanel().let {
                    it.layout = WrapLayout()
                    size = Dimension(1024, 1)
                    add(it); it
                }
                add(JPanel().apply {
                    layout = BorderLayout()
                    startLimit = TimeLimitPanel(0).let { add(it, BorderLayout.WEST); it }
                    endLimit = TimeLimitPanel(1).let { add(it, BorderLayout.EAST); it }
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
                                if (source.selectedThumb > -1) {
                                    presenter.sliderChanged(
                                        source.selectedThumb,
                                        source.thumbPositions[source.selectedThumb]
                                    )
                                }
                            }
                            add(it); it
                        }
                    timeText = JLabel("00:00:00 -> 00:00:00").let { add(it); it }
                })

                add(JPanel().apply {
                    layout = FlowLayout(FlowLayout.RIGHT)
                    JButton("Write").setup { presenter.onWrite() }.let { add(it); it }
                    saveButton = JButton("Save").setup { presenter.onSave(false) }.let { add(it); it }
                    saveNextButton = JButton("Save/Next").setup { presenter.onSave(true) }.let { add(it); it }
                })
            })
        }
    }

    inner class TimeLimitPanel(private val index: Int) : JPanel() {
        val timeText: JLabel

        init {
            add(JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                JButton("-1").setup { presenter.adjustSliderLimit(index, -1f) }.let { add(it); it }
                timeText = JLabel("00:00:00").let { add(it); it }
                JButton("+1").setup { presenter.adjustSliderLimit(index, 1f) }.let { add(it); it }
            })
        }
    }

    override fun setLimits(fromSec: String, toSec: String) {
        controlPanel.startLimit.timeText.text = fromSec
        controlPanel.endLimit.timeText.text = toSec
        controlPanel.updateUI()
    }

    override fun setMarkers(markers: List<Float>) {
        controlPanel.slider.setValues(markers.toFloatArray(), arrayOf(0f, 1f))
    }

    override fun setWordList(words: List<String>) {
        controlPanel.wordPanel.removeAll()
        words.forEachIndexed { i, word ->
            controlPanel.wordPanel.add(JToggleButton(word).setup {
                deselectOthersAction(it)
                presenter.wordSelected(i)
            })
        }
        controlPanel.updateUI()
    }

    override fun setTimeText(text: String) {
        controlPanel.timeText.text = text
    }

    override fun selectWord(index: Int) {
        (controlPanel.wordPanel.components[index] as JToggleButton).let {
            deselectOthers(it)
            it.isSelected = true
        }
    }

}