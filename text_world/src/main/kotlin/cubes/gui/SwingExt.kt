package cubes.gui

import java.awt.Color
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.io.File
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

fun <T : Any> JList<T>.setup(list: List<T>, click: (T) -> Unit): JComponent {
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    setSelectedIndex(0)
//    fixedCellWidth = 200
//    setVisibleRowCount(5)
    model = DefaultListModel<T>().let { model ->
        list.forEach {
            when (it) {
                is File -> model.addElement(it)
            }
        }
        model
    }
    addListSelectionListener {
        if (this@setup.getSelectedIndex() != -1) {
            click(this@setup.getSelectedValue())
        }
    }

    val scroll = JScrollPane(this)
    return scroll
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