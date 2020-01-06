package cubes.gui


//Generated by GuiGenie - Copyright (c) 2004 Mario Awad.
//Home Page http://guigenie.cjb.net - Check often for new versions!

import cubes.gui.Controls.UiObject.*
import cubes.shaders.LineShader
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.drjekyll.fontchooser.FontDialog
import java.awt.*
import java.awt.event.ActionEvent
import javax.swing.*
import kotlin.math.ln


fun main() {
    Controls()
        .setListener(printListener)
        .showWindow()
}

/**
 * TODO make a model object to hold data and an observable with an enum to send event to the presenter (presenter subscribes)
 */
class Controls {

    private lateinit var controlPanel: JPanel
    private lateinit var listener: Listener
    private val events: Subject<Event> = BehaviorSubject.create()

    fun events(): Observable<Event> = events

    data class Event constructor(
        val uiObject: UiObject,
        val data: Any? = null
    )

    enum class UiObject {
        SHADER_LINE_NONE, SHADER_LINE_LINE, SHADER_LINE_GLOW,
        SHADER_BG_NEBULA, SHADER_BG_FLAME, SHADER_BG_REFRACT
    }

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

    inner class MyPanel constructor(listener: Listener) : JPanel() {

        init {
            //construct preComponents
            //val jcomp7Items = arrayOf("circle", "square", "triangle", "flower", "rect", "ngon")
            //selectShaderCombo = JComboBox(jcomp7Items)

            preferredSize = Dimension(800, 700)
            layout = BorderLayout()

            // east panel - shader
            add(JPanel().apply {
                preferredSize = Dimension(200, 400)
                layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
                titledBorder("Shader")

                add(JButton("None").setup { listener.shaderButtonNone() })
                add(JButton("Line").setup { listener.shaderButtonLine() })
                add(JButton("Neon").setup { listener.shaderButtonNeon() })
                add(JPanel().apply { preferredSize = Dimension(10, 20) })
                add(JButton("Nebula").setup { events.onNext(Event(SHADER_BG_NEBULA)) })
                add(JButton("ColdFlame").setup { events.onNext(Event(SHADER_BG_FLAME)) })
                add(JButton("Refraction").setup { events.onNext(Event(SHADER_BG_REFRACT)) })

            }, BorderLayout.EAST)

            // center panel - motion, text
            add(JPanel().apply {
                layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
                // cubes panel
                add(JPanel().apply {
                    layout = GridLayout(-1, 1)//BoxLayout(this, BoxLayout.PAGE_AXIS)
                    titledBorder("Animation")
                    // animation
                    add(JPanel().apply {
                        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
                        add(
                            JSlider(0, 5000)
                                .setup(0, 1, 500, false) {
                                    val source = it.source as JSlider
                                    listener.motionSliderAnimationTime(source.value.toFloat())
                                }
                                .apply { value = 3000 }
                        )
                    })
                })
                add(JPanel().apply {
                    layout = GridLayout(-1, 1)//BoxLayout(this, BoxLayout.PAGE_AXIS)
                    titledBorder("Cubes")

                    // speed
                    add(
                        JPanel().apply {
                            layout = BoxLayout(this, BoxLayout.X_AXIS)
                            add(
                                JSlider(-400, 400)
                                    .setup(0, 1, 200, false) {
                                        val source = it.source as JSlider
                                        listener.motionSliderRotationSpeed(source.value.toFloat())
                                    })
                            add(JButton("0ffest").setup { listener.motionRotationOffsetReset() })
                            add(
                                JSlider(-100, 100)
                                    .setup(0, 1, 50, false) {
                                        val source = it.source as JSlider
                                        listener.motionSliderRotationOffset(source.value.toFloat())
                                    }
                                    .apply { value = 1 }
                            )
                        }.wrapWithLabel("Speed", 100)
                    )

                    // rotation
                    add(
                        JPanel().apply {
                            layout = BoxLayout(this, BoxLayout.X_AXIS)
                            add(JToggleButton("X")
                                .setup(true) { ae -> listener.motionRotX(isSelected(ae)) })
                            add(JToggleButton("Y")
                                .setup(true) { ae -> listener.motionRotY(isSelected(ae)) })
                            add(JToggleButton("Z")
                                .setup(true) { ae -> listener.motionRotZ(isSelected(ae)) })
                            add(JButton("0")
                                .setup { listener.motionRotationReset() })
                            add(JButton("Align").setup { listener.motionAlignExecute() })
                            add(JToggleButton("Visible")
                                .setup(true) { ae -> listener.cubesVisible(isSelected(ae)) })
                        }.wrapWithLabel("Rotation", 100)
                    )

                    // translation
                    add(
                        JPanel().apply {
                            layout = BoxLayout(this, BoxLayout.X_AXIS)
                            add(JButton("grid")
                                .setup { listener.motionGrid() })
                            add(JButton("line")
                                .setup { listener.motionLine() })
                            add(JButton("square")
                                .setup { listener.motionSquare() })
                            add(JButton("0")
                                .setup { listener.motionTranslationReset() })
                        }.wrapWithLabel("Position", 100)
                    )

                    // scale
                    add(
                        JPanel().apply {
                            layout = BoxLayout(this, BoxLayout.X_AXIS)

                            add(
                                JSlider(0, 400)
                                    .setup(0, 1, 200, false) {
                                        val source = it.source as JSlider
                                        listener.motionSliderScale(source.value.toFloat())
                                    }
                            )
                            add(
                                JSlider(0, 400)
                                    .setup(0, 1, 200, false) {
                                        val source = it.source as JSlider
                                        listener.motionSliderScaleDist(source.value.toFloat())
                                    }.wrapWithLabel("Dist")
                            )
                            add(JButton("Apply")
                                .setup { listener.motionApplyScale() })
                        }.wrapWithLabel("Scale", 100)
                    )

                    // fill
                    add(JPanel().apply {
                        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
                        add(JButton("Start").apply {
                            addActionListener {
                                val color = JColorChooser.showDialog(this, "Fill Start Color", Color.WHITE)
                                color?.let {
                                    listener.fillColor(it)
                                    @Suppress("LABEL_NAME_CLASH")
                                    this@apply.background = it
                                }
                            }
                            isOpaque = true
                        })
                        add(JButton("End").apply {
                            addActionListener {
                                val color = JColorChooser.showDialog(this, "Fill End Color", Color.WHITE)
                                color?.let {
                                    listener.fillEndColor(it)
                                    @Suppress("LABEL_NAME_CLASH")
                                    this@apply.background = it
                                }
                            }
                            isOpaque = true
                        })
                        add(JToggleButton("Fill")
                            .setup { ae -> listener.fill(isSelected(ae)) })
                        add(
                            JSlider(0, 255)
                                .setup(0, 1, 64, false) {
                                    val source = it.source as JSlider
                                    listener.fillAlpha(source.value.toFloat())
                                }
                                .apply { value = 3000 }
                                .wrapWithLabel("Alpha")
                        )
                    }.wrapWithLabel("Fill"))

                    // stroke
                    add(JPanel().apply {
                        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
                        add(JButton("Color").apply {
                            addActionListener {
                                val color = JColorChooser.showDialog(this, "Stroke Color", Color.WHITE)
                                color?.let {
                                    listener.strokeColor(it)
                                    @Suppress("LABEL_NAME_CLASH")
                                    this@apply.background = it
                                }
                            }
                            isOpaque = true
                        })
                        add(JToggleButton("Stroke")
                            .setup { ae -> listener.stroke(isSelected(ae)) })
                        add(
                            JSlider(0, 20)
                                .setup(LineShader.DEFAULT_WEIGHT.toInt(), 1, 5, false) {
                                    val source = it.source as JSlider
                                    listener.strokeWeight(source.value.toFloat())
                                }
                        )
                    }.wrapWithLabel("Stroke"))
                })

                add(JPanel().apply {
                    layout = GridLayout(-1, 1)
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
                            add(JButton("Font").apply {
                                var selectedFont: Font? = null
                                addActionListener {
                                    val dialog = FontDialog(null as Frame?, "Font Dialog Example", true)
                                    dialog.selectedFont = selectedFont
                                    dialog.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
                                    dialog.isVisible = true
                                    if (!dialog.isCancelSelected) {
                                        System.out.printf("Selected font is: %s%n", dialog.selectedFont)
                                        selectedFont = dialog.selectedFont
                                        listener.textFont(dialog.selectedFont)
                                    }
                                }
                                isOpaque = true
                            })
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

                    // fill
                    add(JPanel().apply {
                        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
                        add(JButton("Start").apply {
                            addActionListener {
                                val color = JColorChooser.showDialog(this, "Fill Start Color", Color.WHITE)
                                color?.let {
                                    listener.textFillColor(it)
                                    @Suppress("LABEL_NAME_CLASH")
                                    this@apply.background = it
                                }
                            }
                            isOpaque = true
                        })
                        add(JButton("End").apply {
                            addActionListener {
                                val color = JColorChooser.showDialog(this, "Fill End Color", Color.WHITE)
                                color?.let {
                                    listener.textFillEndColor(it)
                                    @Suppress("LABEL_NAME_CLASH")
                                    this@apply.background = it
                                }
                            }
                            isOpaque = true
                        })
                        add(JToggleButton("Fill")
                            .setup { ae -> listener.textFill(isSelected(ae)) })
                        add(
                            JSlider(0, 255)
                                .setup(0, 1, 64, false) {
                                    val source = it.source as JSlider
                                    listener.textFillAlpha(source.value.toFloat())
                                }
                                .apply { value = 255 }
                                .wrapWithLabel("Alpha")
                        )
                    }.wrapWithLabel("Fill"))

                    // stroke
                    add(JPanel().apply {
                        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
                        add(JButton("Color").apply {
                            addActionListener {
                                val color = JColorChooser.showDialog(this, "Stroke Color", Color.WHITE)
                                color?.let {
                                    listener.textStrokeColor(it)
                                    @Suppress("LABEL_NAME_CLASH")
                                    this@apply.background = it
                                }
                            }
                            isOpaque = true
                        })
                        add(JToggleButton("Stroke")
                            .setup { ae -> listener.textStroke(isSelected(ae)) })
                        add(
                            JSlider(0, 20)
                                .setup(LineShader.DEFAULT_WEIGHT.toInt(), 1, 5, false) {
                                    val source = it.source as JSlider
                                    listener.textStrokeWeight(source.value.toFloat())
                                }
                        )
                    }.wrapWithLabel("Stroke"))

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

    interface Listener {
        fun shaderButtonNone()
        fun shaderButtonLine()
        fun shaderButtonNeon()
        fun shaderButtonNebula()
        fun shaderButtonColdFlame()
        fun shaderButtonRefraction()
        fun strokeWeight(value: Float)
        fun motionRotZ(selected: Boolean)
        fun motionRotY(selected: Boolean)
        fun motionRotX(selected: Boolean)
        fun motionAlignExecute()
        fun motionSliderAnimationTime(alignTime: Float)
        fun motionSliderRotationSpeed(value: Float)
        fun motionSliderRotationOffset(offset: Float)
        fun textRandom(selected: Boolean)
        fun textNearRandom(selected: Boolean)
        fun textInOrder(selected: Boolean)
        fun textMotionCube(selected: Boolean)
        fun textMotionAround(selected: Boolean)
        fun textMotionFade(selected: Boolean)
        fun motionRotationReset()
        fun motionGrid()
        fun motionLine()
        fun motionSquare()
        fun motionTranslationReset()
        fun motionSliderScale(scale: Float)
        fun motionApplyScale()
        fun motionSliderScaleDist(dist: Float)
        fun fillColor(color: Color)
        fun fill(selected: Boolean)
        fun fillEndColor(color: Color)
        fun strokeColor(color: Color)
        fun stroke(selected: Boolean)
        fun fillAlpha(alpha: Float)
        fun motionRotationOffsetReset()
        fun textFillColor(color: Color)
        fun textFillEndColor(color: Color)
        fun textFill(selected: Boolean)
        fun textFillAlpha(alpha: Float)
        fun textStrokeColor(color: Color)
        fun textStroke(selected: Boolean)
        fun textStrokeWeight(weight: Float)
        fun textFont(selectedFont: Font)
        fun cubesVisible(selected: Boolean)
    }

    // todo use this to map slider to log values
    fun logslider(position: Int): Double {
        val (minp, minv, scale) = setupLog()

        return Math.exp(minv + scale * (position - minp))
    }

    // todo use this to map log values to slider
    fun logposition(value: Double): Double {
        val (minp, minv, scale) = setupLog()
        return (ln(value) - minv) / scale + minp
    }

    private fun setupLog(): Triple<Int, Double, Double> {
        // position will be between 0 and 100
        val minp = 0
        val maxp = 100

        // The result should be between 100 an 10000000
        val minv = ln(100.0)
        val maxv = ln(10000000.0)

        // calculate adjustment factor
        val scale = (maxv - minv) / (maxp - minp)
        return Triple(minp, minv, scale)
    }
}

val printListener = object : Controls.Listener {
    override fun motionSliderRotationSpeed(value: Float) = println("sliderspeed: $value")
    override fun shaderButtonNone() = println("shader button none")
    override fun shaderButtonLine() = println("shader button line")
    override fun shaderButtonNeon() = println("shader button neon")
    override fun shaderButtonNebula() = println("shader button nebula")
    override fun shaderButtonColdFlame() = println("shader button coldflame")
    override fun shaderButtonRefraction() = println("shader button refract")
    override fun strokeWeight(value: Float) = println("sliderWeight: $value")
    override fun motionRotZ(selected: Boolean) = println("motionRotZ: $selected")
    override fun motionRotY(selected: Boolean) = println("motionRotY: $selected")
    override fun motionRotX(selected: Boolean) = println("motionRotX: $selected")
    override fun motionAlignExecute() = println("motionAlignExecute")
    override fun motionSliderAnimationTime(alignTime: Float) = println("motionSliderAlignTime: $alignTime")
    override fun motionSliderRotationOffset(offset: Float) = println("motionSliderRotationOffset: $offset")
    override fun motionRotationReset() = println("motionResetRotation")
    override fun motionGrid() = println("motionGrid")
    override fun motionLine() = println("motionLine")
    override fun motionSquare() = println("motionSquare")
    override fun motionTranslationReset() = println("motionTranslationResetRotation")
    override fun motionSliderScale(scale: Float) = println("motionSliderScale: $scale")
    override fun motionApplyScale() = println("motionApplyScale")
    override fun motionSliderScaleDist(dist: Float) = println("motionSliderScaleDist: $dist")
    override fun motionRotationOffsetReset() = println("motionRotationOffsetReset")
    override fun fillColor(color: Color) = println("fillColor: $color")
    override fun fill(selected: Boolean) = println("fill: $selected")
    override fun fillEndColor(color: Color) = println("fillEndColor: $color")
    override fun strokeColor(color: Color) = println("strokeColor: $color")
    override fun stroke(selected: Boolean) = println("stroke: $selected")
    override fun textRandom(selected: Boolean) = println("textRandom: $selected")
    override fun textNearRandom(selected: Boolean) = println("textNearRandom: $selected")
    override fun textInOrder(selected: Boolean) = println("textInOrder: $selected")
    override fun textMotionCube(selected: Boolean) = println("textMotionCube: $selected")
    override fun textMotionAround(selected: Boolean) = println("textMotionAround: $selected")
    override fun textMotionFade(selected: Boolean) = println("textMotionFade: $selected")
    override fun fillAlpha(alpha: Float) = println("fillAlpha: $alpha")
    override fun textFillColor(color: Color) = println("textFillColor: $color")
    override fun textFillEndColor(color: Color) = println("textFillEndColor: $color")
    override fun textFill(selected: Boolean) = println("textFill: $selected")
    override fun textFillAlpha(alpha: Float) = println("textFillAlpha: $alpha")
    override fun textStrokeColor(color: Color) = println("textStrokeColor: $color")
    override fun textStroke(selected: Boolean) = println("textStroke: $selected")
    override fun textStrokeWeight(weight: Float) = println("textStrokeWeight: $weight")
    override fun textFont(selectedFont: Font) = println("textFont: $selectedFont")
    override fun cubesVisible(selected: Boolean) = println("cubesVisible: $selected")

}