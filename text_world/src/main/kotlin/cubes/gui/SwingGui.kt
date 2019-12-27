package cubes.gui

import cubes.Cubes
import java.awt.Color
import javax.swing.JButton
import javax.swing.JColorChooser
import javax.swing.JFrame

class SwingGui(sketch: Cubes) {
    private val frame: JFrame
    fun show() {
        frame.isVisible = true
    }

    init {
        frame = JFrame("Controls")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        val pickColor = JButton("Color...")
        pickColor.addActionListener {
            sketch.color = JColorChooser.showDialog(pickColor, "Color Picker", sketch.color)
        }
        frame.add(pickColor)
        frame.setSize(200, 100)
    }
}