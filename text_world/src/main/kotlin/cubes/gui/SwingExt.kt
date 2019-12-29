package cubes.gui

import java.awt.*
import java.awt.event.ActionEvent
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.event.ChangeEvent

fun JSlider.setup(
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
    return this
}

fun JButton.setup(click: (ActionEvent) -> Unit): JButton {
    addActionListener(click)
    return this
}

fun JToggleButton.setup(click: (ActionEvent) -> Unit): JToggleButton {
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

fun JComponent.wrapWithLabel(label: String, labelWidth: Int = 80): JPanel {
    JPanel().apply {
        //background = Color.red
        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
        //layout = FlowLayout()
        add(JLabel(label).apply {
            preferredSize = Dimension(labelWidth, 20)
        })
        add(this@wrapWithLabel)
        return this
    }

}