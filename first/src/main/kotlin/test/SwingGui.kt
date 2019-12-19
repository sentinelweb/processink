package test

import java.awt.Color
import javax.swing.JButton
import javax.swing.JColorChooser
import javax.swing.JFrame

class SwingGui(sketch: Test1) {
    private val frame: JFrame
    fun show() {
        frame.isVisible = true
    }

    init {
        frame = JFrame("Controls")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        val pickColor = JButton("Color...")
        pickColor.addActionListener {
            val color = JColorChooser.showDialog(pickColor, "Color Picker", Color.RED)
            sketch.setColor(color.red, color.green, color.blue)
        }
        frame.add(pickColor)
        frame.setSize(200, 100)
    }
}