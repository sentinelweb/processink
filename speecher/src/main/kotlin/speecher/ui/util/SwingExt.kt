package speecher.ui.util

import speecher.ui.image.Image
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.event.ActionEvent
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.event.ChangeEvent
import javax.swing.text.JTextComponent

val backgroundColor = Color.WHITE

fun JSlider.setup(
    initial: Int?,
    minor: Int,
    major: Int,
    paintTicks: Boolean = false,
    change: (ChangeEvent) -> Unit
): JSlider {
    setOrientation(JSlider.HORIZONTAL)
    setMinorTickSpacing(minor)
    setMajorTickSpacing(major)
    setPaintTicks(paintTicks)
    setPaintLabels(true)
    addChangeListener(change)
    initial?.let { value = it }
    return this
}

fun JButton.setup(click: (ActionEvent) -> Unit): JButton {
    addActionListener(click)
    return this
}

fun JToggleButton.setup(selected: Boolean = false, click: (ActionEvent) -> Unit): JToggleButton {
    isSelected = selected
    addActionListener(click)
    return this
}

fun JComponent.setup(): JComponent {
    return this
}

val defaultTextSize = 12
fun JPanel.titledBorder(title: String, padding: Int = 10): JPanel {
    border = BorderFactory.createCompoundBorder(
        BorderFactory.createCompoundBorder(
            EmptyBorder(padding, padding, padding, padding),
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), title)
                .apply { titleFont = defaultFont() }
        ), EmptyBorder(padding, padding, padding, padding)
    )
    background = backgroundColor
    return this
}

fun JComponent.wrapWithLabel(label: String, labelWidth: Int = 80, iconName: String? = null): JPanel =
    JPanel().apply {
        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
        background = backgroundColor
        add(JLabel(label).style(bold = true).apply {
            preferredSize = Dimension(labelWidth, 20)
            border = EmptyBorder(0, 0, 0, 10)
            background = backgroundColor
            iconName?.let { icon = Image(it) }
        })
        add(this@wrapWithLabel)
        return this
    }

fun isSelected(ae: ActionEvent) = (ae.source as JToggleButton).isSelected

fun isSelectedDeselectOthers(ae: ActionEvent): Boolean {
    val jToggleButton = ae.source as JToggleButton
    val selected = jToggleButton.isSelected
    jToggleButton.parent.components.forEach { component ->
        if (component != jToggleButton && component is JToggleButton) {
            component.isSelected = false
        }
    }
    return selected
}

fun deselectOthersAction(ae: ActionEvent): Boolean {
    val jToggleButton = ae.source as JToggleButton
    val selected = jToggleButton.isSelected
    deselectOthers(jToggleButton)
    return selected
}

fun deselectOthers(jToggleButton: JToggleButton) {
    jToggleButton.parent.components.forEach { component ->
        if (component != jToggleButton && component is JToggleButton) {
            component.isSelected = false
        }
    }
}

inline fun <reified T : AbstractButton> T.style(size: Int = defaultTextSize, bold: Boolean = false): T {
    apply { setFont(defaultFont(size, bold)) }
    return this
}

fun JLabel.style(size: Int = defaultTextSize, bold: Boolean = false): JLabel {
    apply { setFont(defaultFont(size, bold)) }
    return this
}

inline fun <reified T : JTextComponent> T.style(size: Int = defaultTextSize, bold: Boolean = false): T {
    apply { setFont(defaultFont(size, bold)) }
    return this
}

fun defaultFont(size: Int = defaultTextSize, bold: Boolean = false) =
    Font("Arial", if (bold) Font.BOLD else Font.PLAIN, size)

inline fun <reified T : AbstractButton> T.icon(name: String): T {
    apply { icon = ImageIcon(this::class.java.getResource("/images/$name")) }
    return this
}

fun JLabel.icon(name: String): JLabel {
    apply { icon = ImageIcon(this::class.java.getResource("/images/$name")) }
    return this
}
