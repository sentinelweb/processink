package cubes.gui


//Generated by GuiGenie - Copyright (c) 2004 Mario Awad.
//Home Page http://guigenie.cjb.net - Check often for new versions!

import cubes.shaders.LineShader
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.ActionEvent
import javax.swing.*

fun main() {
    val printListener = object : Controls.Listener {
        override fun motionSliderSpeed(value: Float) = println("sliderspeed: $value")
        override fun shaderButtonNone() = println("shader button none")
        override fun shaderButtonLine() = println("shader button line")
        override fun shaderButtonNeon() = println("shader button neon")
        override fun shaderSliderWeight(value: Float) = println("sliderWeight: $value")
        override fun motionRotZ(selected: Boolean) = println("motionRotZ: $selected")
        override fun motionRotY(selected: Boolean) = println("motionRotY: $selected")
        override fun motionRotX(selected: Boolean) = println("motionRotX: $selected")
        override fun motionAlignExecute() = println("motionAlignExecute")
        override fun motionSliderAlignTime(alignTime: Float) = println("motionSliderAlignTime :$alignTime")
        override fun textRandom(selected: Boolean) = println("textRandom: $selected")
        override fun textNearRandom(selected: Boolean) = println("textNearRandom: $selected")
        override fun textInOrder(selected: Boolean) = println("textInOrder: $selected")
        override fun textMotionCube(selected: Boolean) = println("textMotionCube: $selected")
        override fun textMotionAround(selected: Boolean) = println("textMotionAround: $selected")
        override fun textMotionFade(selected: Boolean) = println("textMotionFade: $selected")
        override fun motionResetRotation() = println("motionResetRotation")
    }
    Controls()
        .setListener(printListener)
        .showWindow()
}

class Controls constructor() {

    private lateinit var controlPanel: JPanel
    private lateinit var listener: Listener

    fun showWindow() {
        SwingUtilities.invokeLater {
            val frame = JFrame("Controls")
            frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

            controlPanel = MyPanel(listener)
            //controlPanel.setOpaque(true) //content panes must be opaque

            frame.add(controlPanel)

            // Display the window.
            frame.pack()
            frame.isVisible = true
        }
    }

    fun setListener(listener: Listener): Controls {
        this.listener = listener
        return this
    }

    interface Listener {
        fun motionSliderSpeed(value: Float)
        fun shaderButtonNone()
        fun shaderButtonLine()
        fun shaderButtonNeon()
        fun shaderSliderWeight(value: Float)
        fun motionRotZ(selected: Boolean)
        fun motionRotY(selected: Boolean)
        fun motionRotX(selected: Boolean)
        fun motionAlignExecute()
        fun motionSliderAlignTime(alignTime: Float)
        fun textRandom(selected: Boolean)
        fun textNearRandom(selected: Boolean)
        fun textInOrder(selected: Boolean)
        fun textMotionCube(selected: Boolean)
        fun textMotionAround(selected: Boolean)
        fun textMotionFade(selected: Boolean)
        fun motionResetRotation()
    }

    inner class MyPanel constructor(listener: Listener) : JPanel() {

        init {
            //construct preComponents
            //val jcomp7Items = arrayOf("circle", "square", "triangle", "flower", "rect", "ngon")
            //selectShaderCombo = JComboBox(jcomp7Items)

            setPreferredSize(Dimension(800, 400))
            setLayout(BorderLayout())

            // east panel - shader
            add(JPanel().apply {
                preferredSize = Dimension(200, 400)
                layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
                titledBorder("Shader")

                add(JButton("None").setup { listener.shaderButtonNone() })
                add(JButton("Line").setup { listener.shaderButtonLine() })
                add(JButton("Neon").setup { listener.shaderButtonNeon() })
                add(
                    JSlider(0, 20)
                        .setup(LineShader.DEFAULT_WEIGHT.toInt(), 1, 5, false, {
                            val source = it.source as JSlider
                            listener.shaderSliderWeight(source.value.toFloat())
                        })
                )
            }, BorderLayout.EAST)

            // center panel - motion, text
            add(JPanel().apply {
                layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
                // motion panel
                add(JPanel().apply {
                    layout = GridLayout(-1, 1)//BoxLayout(this, BoxLayout.PAGE_AXIS)
                    titledBorder("Cube Motion")
                    // speed
                    add(
                        JSlider(-400, 400)
                            .setup(0, 1, 50, true, {
                                val source = it.source as JSlider
                                listener.motionSliderSpeed(source.value.toFloat() / 10f)
                            }).wrapWithLabel("Speed")
                    )
                    // axis
                    add(
                        JPanel().apply {
                            layout = BoxLayout(this, BoxLayout.X_AXIS)
                            add(JToggleButton("X")
                                .setup(true) { ae -> listener.motionRotX(isSelected(ae)) })
                            add(JToggleButton("Y")
                                .setup(true) { ae -> listener.motionRotY(isSelected(ae)) })
                            add(JToggleButton("Z")
                                .setup(true) { ae -> listener.motionRotZ(isSelected(ae)) })
                            add(JButton("0").setup { listener.motionResetRotation() })
                        }.wrapWithLabel("RotationAxis", 100)
                    )

                    // align
                    add(JPanel().apply {
                        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
                        add(
                            JSlider(0, 2000)
                                .setup(0, 1, 500, false, {
                                    val source = it.source as JSlider
                                    listener.motionSliderAlignTime(source.value.toFloat())
                                })
                        )
                        add(JButton("Execute").setup { listener.motionAlignExecute() })
                    }.wrapWithLabel("Align time"))
                })

                add(JPanel().apply {
                    layout = GridLayout(-1, 1)//BoxLayout(this, BoxLayout.PAGE_AXIS)
                    titledBorder("Text Control")
                    // order
                    add(
                        JPanel().apply {
                            layout = BoxLayout(this, BoxLayout.X_AXIS)
                            add(JToggleButton("Random")
                                .setup { ae -> listener.textRandom(isSelectedDeselectOthers(ae)) })
                            add(JToggleButton("Near Random")
                                .setup { ae -> listener.textNearRandom(isSelectedDeselectOthers(ae)) })
                            add(JToggleButton("In order")
                                .setup { ae -> listener.textInOrder(isSelectedDeselectOthers(ae)) })
                        }.wrapWithLabel("Ordering")
                    )

                    // motion
                    add(
                        JPanel().apply {
                            layout = BoxLayout(this, BoxLayout.X_AXIS)
                            add(JToggleButton("With Cube")
                                .setup { ae -> listener.textMotionCube(isSelectedDeselectOthers(ae)) })
                            add(JToggleButton("Around")
                                .setup { ae -> listener.textMotionAround(isSelectedDeselectOthers(ae)) })
                            add(JToggleButton("Fade")
                                .setup { ae -> listener.textMotionFade(isSelectedDeselectOthers(ae)) })
                        }.wrapWithLabel("Motion")
                    )

                })
            }, BorderLayout.CENTER)
        }

        private fun isSelected(ae: ActionEvent) = (ae.source as JToggleButton).isSelected

        private fun isSelectedDeselectOthers(ae: ActionEvent): Boolean {
            val jToggleButton = ae.source as JToggleButton
            val selected = jToggleButton.isSelected
            jToggleButton.parent.components.forEach { component ->
                if (component != jToggleButton && component is JToggleButton) {
                    component.isSelected = false
                }
            }
            return selected
        }

    }


}