package cubes.gui


//Generated by GuiGenie - Copyright (c) 2004 Mario Awad.
//Home Page http://guigenie.cjb.net - Check often for new versions!

import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

fun main() {
    Controls(object : Controls.MyPanel.Listener {
        override fun sliderSpeed(value: Float) = println("slider1: $value")
        override fun slider2(value: Float) = println("slider2: $value")
        override fun slider3(value: Float) = println("slider3: $value")
        override fun buttonNone() = println("button1")
        override fun buttonLine() = println("button2")
        override fun buttonNeon() = println("button3")
        override fun sliderWeight(value: Float) = println("sliderWeight: $value")
    })
}

class Controls constructor(cubesApplet: MyPanel.Listener) {

    private val controlPanel: JPanel

    init {
        val frame = JFrame("Controls")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        controlPanel = MyPanel(cubesApplet)
        //controlPanel.setOpaque(true) //content panes must be opaque

        frame.add(controlPanel)

        // Display the window.
        frame.pack()
        frame.isVisible = true
    }

    class MyPanel constructor(val listener: Listener) : JPanel() {

        interface Listener {
            fun sliderSpeed(value: Float)
            fun slider2(value: Float)
            fun slider3(value: Float)
            fun buttonNone()
            fun buttonLine()
            fun buttonNeon()
            fun sliderWeight(value: Float)
        }

        private var buttonNone: JButton
        private var buttonLine: JButton
        private var buttonNeon: JButton
        private var shaderSliderWeight: JSlider
        private var sliderSpeed: JSlider
        private var slider2: JSlider
        private var slider3: JSlider
        //private var selectShaderCombo: JComboBox<String>
        private var motionLabel = JLabel("Motion")
        private var speedLabel = JLabel("Speed")

        init {
            //construct preComponents
            //val jcomp7Items = arrayOf("circle", "square", "triangle", "flower", "rect", "ngon")
            //selectShaderCombo = JComboBox(jcomp7Items)

            setPreferredSize(Dimension(800, 400))
            setLayout(BorderLayout())
            //construct components
            buttonNone = JButton("None").setup { listener.buttonNone() }
            buttonLine = JButton("Line").setup { listener.buttonLine() }
            buttonNeon = JButton("Neon").setup { listener.buttonNeon() }
            shaderSliderWeight = JSlider(0, 200)
                .setup(1, 50) {
                    val source = it.source as JSlider
                    listener.sliderWeight(source.value.toFloat() / 10f)
                }

            add(JPanel().apply {
                preferredSize = Dimension(200, 400)
                layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
                add(buttonNone)
                add(buttonLine)
                add(buttonNeon)
                add(shaderSliderWeight)
            }, BorderLayout.EAST)

            sliderSpeed = JSlider(-400, 400)
                .setup(1, 50) {
                    val source = it.source as JSlider
                    listener.sliderSpeed(source.value.toFloat()/10f)
                }
            slider2 = JSlider(0, 20)
                .setup(1, 5) {
                    val source = it.source as JSlider
                    listener.slider2(source.value.toFloat())
                }
            slider3 = JSlider(0, 20)
                .setup(1, 5) {
                    val source = it.source as JSlider
                    listener.slider3(source.value.toFloat())
                }

            add( JPanel().apply {
                layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
                add(sliderSpeed)
                add(slider2)
                add(slider3)
            }, BorderLayout.CENTER)
        }

    }


}