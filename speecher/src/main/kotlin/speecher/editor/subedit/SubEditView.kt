package speecher.editor.subedit

import org.koin.core.context.startKoin
import speecher.di.Modules
import speecher.domain.Subtitles
import speecher.editor.subedit.word_timeline.WordTimelineContract
import speecher.editor.subedit.word_timeline.WordTimelineView
import speecher.ui.layout.wrap.WrapLayout
import speecher.ui.multithumbslider.MultiThumbSlider
import speecher.ui.util.*
import java.awt.*
import javax.swing.*
import javax.swing.BoxLayout.PAGE_AXIS

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
    private val bgColor: Color = backgroundColor
    override val wordTimelineExt: WordTimelineContract.External
        get() = controlPanel.wordTimeline.external

    override fun showWindow() {
        SwingUtilities.invokeLater {
            val frame = JFrame("Edit subtitles")
            frame.defaultCloseOperation = JFrame.HIDE_ON_CLOSE
            frame.background = bgColor
            controlPanel = SubEditPanel()
            frame.add(controlPanel)
            frame.location = Point(300, 370)
            // Display the window.
            frame.pack()
            frame.isVisible = true
            presenter.onInitialised()
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
        val wordTimeline: WordTimelineView

        init {
            background = bgColor
            add(JPanel().apply {
                layout = BoxLayout(this, PAGE_AXIS)
                preferredSize = Dimension(1024, 220)
                background = bgColor
                wordPanel = JPanel().apply {
                    layout = WrapLayout()
                    background = bgColor
                    size = Dimension(1024, 80)
                }.also { add(it) }

                JPanel().apply {
                    layout = BorderLayout()
                    background = bgColor
                    startLimit = TimeLimitPanel(0).let { add(it, BorderLayout.WEST); it }
                    endLimit = TimeLimitPanel(1).let { add(it, BorderLayout.EAST); it }
                }.also { add(it) }

                JPanel().apply {
                    layout = GridLayout(-1, 1)
                    background = bgColor
                    slider = MultiThumbSlider<Float>(floatArrayOf(0.1f, 0.2f), arrayOf(1f, 2f))
                        .let {
                            // it.preferredSize = Dimension(1000, 60)
                            it.collisionPolicy = MultiThumbSlider.Collision.STOP_AGAINST
                            it.isThumbRemovalAllowed = false
                            // it.isAutoAdding = false
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
                    wordTimeline = WordTimelineView().let { add(it); it }
                    timeText = JLabel("00:00:00 -> 00:00:00").style().let { add(it); it }
                }.also { add(it) }

                JPanel().apply {
                    layout = FlowLayout(FlowLayout.RIGHT)
                    background = bgColor
                    JButton("Write").style().setup { presenter.onWrite() }.let { add(it); it }
                    saveButton = JButton("Save").style().setup { presenter.onSave(false) }.let { add(it); it }
                    saveNextButton = JButton("Save/Next").style().setup { presenter.onSave(true) }.let { add(it); it }
                }.also { add(it) }
            })
        }
    }

    inner class TimeLimitPanel(private val index: Int) : JPanel() {
        val timeText: JLabel

        init {
            background = bgColor
            add(JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                background = bgColor
                JButton("-1").style().setup { presenter.adjustSliderLimit(index, -1f) }.let { add(it); it }
                timeText = JLabel("00:00:00").style().let { add(it); it }
                JButton("+1").style().setup { presenter.adjustSliderLimit(index, 1f) }.let { add(it); it }
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
            controlPanel.wordPanel.add(JToggleButton(word).style().setup {
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