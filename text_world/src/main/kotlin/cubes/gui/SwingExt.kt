package cubes.gui

import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JSlider
import javax.swing.event.ChangeEvent

fun JSlider.setup(minor: Int, major: Int, change: (ChangeEvent) -> Unit): JSlider {
    setOrientation(JSlider.HORIZONTAL)
    setMinorTickSpacing(minor)
    setMajorTickSpacing(major)
    setPaintTicks(true)
    setPaintLabels(true)
    addChangeListener(change)
    return this
}

fun JButton.setup(click:(ActionEvent) -> Unit):JButton {
    addActionListener(click)
    return this
}