package speecher.generator.ui

import speecher.domain.Sentence
import speecher.domain.Subtitles
import speecher.generator.ui.SpeechContract.WordParamType.*
import speecher.ui.util.backgroundColor
import speecher.ui.util.setup
import speecher.ui.util.style
import speecher.util.format.TimeFormatter
import java.awt.*
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

class WordChipView constructor(
    private val timeFormatter: TimeFormatter,
    private val word: Sentence.Word,
    private val index: Int,
    private val listener: SpeechContract.WordListener
) : JPanel() {

    var interfaceVisible: Boolean = false
        get() = field
        set(value) {
            field = value
            showHideToggles(value)
        }
    private val bgColor: Color = backgroundColor
    private val subChip: SubtitleChipView

    //private val beforeToggle: ToggleButtons
    private val afterToggle: ToggleButtons
    private val speedToggle: ToggleButtons
    private val volToggle: ToggleButtons
    private val fromToggle: ToggleButtons
    private val toToggle: ToggleButtons
    private val toggleContainer: JPanel

    var selected = false
        set(value) {
            field = value
            subChip.selected = value
        }

    private val subtitleChipViewListener = object : SpeechContract.SubListener {

        override fun onItemClicked(sub: Subtitles.Subtitle, metas: List<SpeechContract.MetaKey>) {
            listener.onItemClicked(index, metas)
        }

        override fun onPreviewClicked(sub: Subtitles.Subtitle) {
            listener.onPreviewClicked(index)
        }
    }

    init {
        layout = BorderLayout()
        background = bgColor
        JLabel(word.sub.text[0]).style()
            .also { add(it, BorderLayout.NORTH) }

        toggleContainer = JPanel().apply {
            layout = CardLayout()
            background = bgColor
            subChip = SubtitleChipView(timeFormatter, word.sub, subtitleChipViewListener)
                .apply {
                }
                .also { add(it, "chip") }
            JPanel().apply {
                background = bgColor
                layout = FlowLayout()
                JPanel().apply {
                    background = bgColor
                    layout = GridLayout(1, -1)
//                    beforeToggle = ToggleButtons(value = word.spaceBefore, type = BEFORE, tooltip = "Before")
//                        .also { add(it) }
                    volToggle = ToggleButtons(value = word.vol, type = VOL, tooltip = "Vol")
                        .also { add(it) }

                    speedToggle = ToggleButtons(value = word.speed, type = SPEED, tooltip = "Speed")
                        .also { add(it) }

                    afterToggle = ToggleButtons(value = word.spaceAfter, type = AFTER, tooltip = "After")
                        .also { add(it) }
                }.also { add(it) }
                JPanel().apply {
                    background = bgColor
                    layout = GridLayout(1, -1)
                    fromToggle =
                        ToggleButtons(value = word.sub.fromSec, type = FROM, wid = 40, tooltip = "From")
                            .also { add(it) }
                    toToggle = ToggleButtons(value = word.sub.toSec, type = TO, wid = 40, tooltip = "To")
                        .also { add(it) }
                }.also { add(it) }
            }.also { add(it, "toggle") }
        }.also { add(it, BorderLayout.CENTER) }
        showHideToggles(interfaceVisible)
    }

    private fun showHideToggles(value: Boolean) {
        (toggleContainer.layout as CardLayout).show(toggleContainer, if (value) TOGGLE else CHIP)
    }

    inner class ToggleButtons constructor(
        private var value: Float = 0f,
        private var increment: Float = 0.01f,
        private var incrementBig: Float = 0.1f,
        private val type: SpeechContract.WordParamType,
        private val horizontal: Boolean = false,
        private val wid: Int = 25,
        private val tooltip: String? = null
    ) : JPanel() {

        private val rows: Int
            get() = if (horizontal) 1 else -1
        private val cols: Int
            get() = if (!horizontal) 1 else -1

        private val upButton: JButton
        private val valueLabel: JLabel
        private val downButton: JButton

        init {
            layout = GridLayout(rows, cols)
            preferredSize = Dimension(wid, 40)
            background = bgColor
            valueLabel = JLabel().style(10).apply { toolTipText = tooltip }

            upButton = JButton("+").style(bold = true)
                .apply { size = Dimension(wid, 20) }
                .setup { ae ->
                    updateVal(value + getIncrement(ae))
                    sendValue()
                }
                .also { add(it) }
            valueLabel.also { add(it) }
            downButton = JButton("-").style(bold = true)
                .apply { size = Dimension(wid, 20) }
                .setup { ae ->
                    updateVal(value - getIncrement(ae))
                    sendValue()
                }
                .also { add(it) }

            updateVal(value)
        }

        private fun getIncrement(ae: ActionEvent): Float =
            if (ae.modifiers and ActionEvent.SHIFT_MASK == ActionEvent.SHIFT_MASK) incrementBig else increment


        fun updateVal(newValue: Float) {
            value = "%.2f".format(newValue).toFloat()
            valueLabel.text = value.toString()
        }

        private fun sendValue() {
            listener.changed(index, type, value)
        }
    }

    companion object {
        const val TOGGLE = "toggle"
        const val CHIP = "chip"
    }
}