package cubes.gui

import processing.core.PApplet
import java.awt.Color
import java.awt.Dimension
import java.awt.event.ActionEvent
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.event.ChangeEvent

fun JSlider.setup(
    initial: Int,
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
    value = initial
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

fun JPanel.titledBorder(title: String, padding: Int = 10): JPanel {
    border = BorderFactory.createCompoundBorder(
        BorderFactory.createCompoundBorder(
            EmptyBorder(padding, padding, padding, padding),
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), title)
        ), EmptyBorder(padding, padding, padding, padding)
    )
    //background = Color.lightGray
    return this
}

fun JComponent.wrapWithLabel(label: String, labelWidth: Int = 80): JPanel =
    JPanel().apply {
        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
        add(JLabel(label).apply {
            preferredSize = Dimension(labelWidth, 20)
        })
        add(this@wrapWithLabel)
        return this
    }

fun Color.toProcessing(p: PApplet) = p.color(red, green, blue, alpha)
fun Color.toProcessing(alpha: Float, p: PApplet) = p.color(red, green, blue, alpha.toInt())