package cubes.gui


//Generated by GuiGenie - Copyright (c) 2004 Mario Awad.
//Home Page http://guigenie.cjb.net - Check often for new versions!

import cubes.CubesContract.BackgroundShaderType.*
import cubes.gui.Controls.UiObject.*
import cubes.shaders.LineShader
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.drjekyll.fontchooser.FontDialog
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.io.File
import javax.swing.*

fun main() {
    Controls()
        .showWindow()
}

/**
 * TODO make a model object to hold data
 */
class Controls {

    private lateinit var controlPanel: ControlsPanel
    private val events: Subject<Event> = BehaviorSubject.create()

    fun events(): Observable<Event> = events

    // todo move to state
    private var filesDir: File = File(System.getProperty("user.home"), "cubes")
    private var stateDir: File = File(filesDir, "state")
    private var textDir: File = File(filesDir, "text")

    private var stateFile: File = File(stateDir, "state.json")
    private var textFile: File = File(textDir, "text.txt")

    data class Event constructor(
        val uiObject: UiObject,
        val data: Any? = null
    )

    enum class UiObject {
        SHADER_LINE_NONE, SHADER_LINE_LINE, SHADER_LINE_NEON,
        SHADER_BG, SHADER_BG_COLOR,
        MOTION_ANIMATION_TIME,
        CUBES_ROTATION_SLIDER,
        CUBES_ROTATION_OFFEST_RESET,
        CUBES_ROTATION_OFFEST_SLIDER,
        CUBES_ROTATION_X,
        CUBES_ROTATION_Y,
        CUBES_ROTATION_Z,
        CUBES_ROTATION_RESET,
        CUBES_ROTATION_ALIGN,
        CUBES_VISIBLE,
        CUBES_GRID,
        CUBES_LINE,
        CUBES_SQUARE,
        CUBES_TRANSLATION_RESET,
        CUBES_SCALE_BASE_SLIDER,
        CUBES_SCALE_OFFSET_SLIDER,
        CUBES_SCALE_APPLY,
        CUBES_COLOR_FILL_START,
        CUBES_COLOR_FILL_END,
        CUBES_FILL,
        CUBES_COLOR_FILL_ALPHA,
        CUBES_COLOR_STROKE,
        CUBES_STROKE,
        CUBES_STROKE_WEIGHT,
        TEXT_ORDER_RANDOM,
        TEXT_ORDER_NEAR_RANDOM,
        TEXT_ORDER_INORDER,
        TEXT_FONT,
        TEXT_MOTION_CUBE,
        TEXT_MOTION_AROUND,
        TEXT_MOTION_FADE,
        TEXT_COLOR_FILL,
        TEXT_COLOR_FILL_END,
        TEXT_FILL,
        TEXT_FILL_ALPHA,
        TEXT_COLOR_STROKE,
        TEXT_STROKE_WEIGHT,
        TEXT_STROKE,
        MENU_OPEN_STATE,
        MENU_SAVE_STATE,
        MENU_OPEN_TEXT,
        MENU_SAVE_TEXT,
    }

    fun showWindow() {
        SwingUtilities.invokeLater {
            val frame = JFrame("Controls")
            frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            val menuBar = makeMenu()
            controlPanel = ControlsPanel()
            //controlPanel.setOpaque(true) //content panes must be opaque

            frame.add(controlPanel)
            frame.setJMenuBar(menuBar)
            // Display the window.
            frame.pack()
            frame.isVisible = true
        }
    }

    private fun showOpenDialog(title: String, currentDir: File?, chosen: (File) -> Unit) {
        JFileChooser().apply {
            isMultiSelectionEnabled = false
            fileSelectionMode = JFileChooser.FILES_ONLY
            currentDir.let { currentDirectory = it }
            val result = showOpenDialog(controlPanel)
            if (result == JFileChooser.APPROVE_OPTION) {
                chosen(selectedFile)
            }
        }
    }

    private fun showSaveDialog(title: String, currentDir: File?, chosen: (File) -> Unit) {
        JFileChooser().apply {
            isMultiSelectionEnabled = false
            fileSelectionMode = JFileChooser.FILES_ONLY
            currentDir.let { currentDirectory = it }
            val result = showSaveDialog(controlPanel)
            if (result == JFileChooser.APPROVE_OPTION) {
                chosen(selectedFile)
            }
        }
    }

    inner class ControlsPanel constructor() : JPanel() {
        lateinit var stateList: JList<File>
        lateinit var textList: JList<File>

        init {
            //construct preComponents
            //val jcomp7Items = arrayOf("circle", "square", "triangle", "flower", "rect", "ngon")
            //selectShaderCombo = JComboBox(jcomp7Items)

            preferredSize = Dimension(1100, 700)
            layout = BorderLayout()

            // east panel - shader
            add(JPanel().apply {
                preferredSize = Dimension(200, 400)
                layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
                add(JPanel().apply {
                    titledBorder("State")
                    val stateFiles = stateDir.listFiles()?.toList() ?: listOf()
                    JList<File>()
                        .also { stateList = it }
                        .setup(stateFiles) { events.onNext(Event(MENU_OPEN_STATE, it)) }
                        .also { add(it) }

                })
                add(JPanel().apply {
                    titledBorder("Text")
                    val textFiles = textDir.listFiles()?.toList() ?: listOf()
                    JList<File>()
                        .also { textList = it }
                        .setup(textFiles) { events.onNext(Event(MENU_OPEN_TEXT, it)) }
                        .also { add(it) }

                })
            }, BorderLayout.WEST)

            // east panel - shader
            add(JPanel().apply {
                preferredSize = Dimension(200, 400)
                layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
                titledBorder("Shader")

                add(JButton("None").setup { events.onNext(Event(SHADER_LINE_NONE)) })
                add(JButton("Line").setup { events.onNext(Event(SHADER_LINE_LINE)) })
                add(JButton("Neon").setup { events.onNext(Event(SHADER_LINE_NEON)) })
                add(JPanel().apply { preferredSize = Dimension(10, 20) })
                add(JButton("Color").apply {
                    addActionListener {
                        val color = JColorChooser.showDialog(this, "Background Color", Color.WHITE)
                        color?.let {
                            events.onNext(Event(SHADER_BG_COLOR, it))

                            @Suppress("LABEL_NAME_CLASH")
                            this@apply.background = it
                        }
                    }
                })
                add(JButton("None").setup { events.onNext(Event(SHADER_BG, NONE)) })
                add(JButton("Nebula").setup { events.onNext(Event(SHADER_BG, NEBULA)) })
                add(JButton("ColdFlame").setup { events.onNext(Event(SHADER_BG, COLDFLAME)) })
                add(JButton("Refraction").setup { events.onNext(Event(SHADER_BG, REFRACTION_PATTERN)) })
                add(JButton("Deform").setup { events.onNext(Event(SHADER_BG, DEFORM)) })
                add(JButton("Monjori").setup { events.onNext(Event(SHADER_BG, MONJORI)) })
                add(JButton("Water").setup { events.onNext(Event(SHADER_BG, WATER)) })
                add(JButton("Fuji").setup { events.onNext(Event(SHADER_BG, FUJI)) })
                add(JButton("Fractal pyramid").setup { events.onNext(Event(SHADER_BG, FRACTAL_PYRAMID)) })
                add(JButton("Octagrams").setup { events.onNext(Event(SHADER_BG, OCTAGRAMS)) })
                add(JButton("Protean clouds").setup { events.onNext(Event(SHADER_BG, PROTEAN_COUDS)) })
                add(JButton("Eclipse").setup { events.onNext(Event(SHADER_BG, ECLIPSE)) })
                add(JButton("OneWarp").setup { events.onNext(Event(SHADER_BG, ONEWARP)) })
//                add(JButton("Clouds").setup { events.onNext(Event(SHADER_BG, CLOUDS)) })

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
                                    events.onNext(Event(MOTION_ANIMATION_TIME, source.value.toFloat()))
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
                                        events.onNext(Event(CUBES_ROTATION_SLIDER, source.value.toFloat()))
                                    })
                            add(JButton("0ffest").setup { events.onNext(Event(CUBES_ROTATION_OFFEST_RESET)) })
                            add(
                                JSlider(-100, 100)
                                    .setup(0, 1, 50, false) {
                                        val source = it.source as JSlider
                                        events.onNext(Event(CUBES_ROTATION_OFFEST_SLIDER, source.value.toFloat()))
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
                                .setup(true) { ae -> events.onNext(Event(CUBES_ROTATION_X, isSelected(ae))) })
                            add(JToggleButton("Y")
                                .setup(true) { ae -> events.onNext(Event(CUBES_ROTATION_Y, isSelected(ae))) })
                            add(JToggleButton("Z")
                                .setup(true) { ae -> events.onNext(Event(CUBES_ROTATION_Z, isSelected(ae))) })
                            add(JButton("0")
                                .setup { events.onNext(Event(CUBES_ROTATION_RESET)) })
                            add(JButton("Align").setup { events.onNext(Event(CUBES_ROTATION_ALIGN)) })
                            add(JToggleButton("Visible")
                                .setup(true) { ae -> events.onNext(Event(CUBES_VISIBLE, isSelected(ae))) })
                        }
                            .wrapWithLabel("Rotation", 100))

                    // translation
                    add(
                        JPanel().apply {
                            layout = BoxLayout(this, BoxLayout.X_AXIS)
                            add(JButton("grid")
                                .setup { events.onNext(Event(CUBES_GRID)) })
                            add(JButton("line")
                                .setup { events.onNext(Event(CUBES_LINE)) })
                            add(JButton("square")
                                .setup { events.onNext(Event(CUBES_SQUARE)) })
                            add(JButton("0")
                                .setup { events.onNext(Event(CUBES_TRANSLATION_RESET)) })
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
                                        events.onNext(Event(CUBES_SCALE_BASE_SLIDER, source.value.toFloat()))
                                    }
                            )
                            add(
                                JSlider(0, 400)
                                    .setup(0, 1, 200, false) {
                                        val source = it.source as JSlider
                                        events.onNext(Event(CUBES_SCALE_OFFSET_SLIDER, source.value.toFloat()))
                                    }.wrapWithLabel("Dist")
                            )
                            add(JButton("Apply").setup { events.onNext(Event(CUBES_SCALE_APPLY)) })
                        }.wrapWithLabel("Scale", 100)
                    )

                    // fill
                    add(JPanel().apply {
                        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
                        add(JButton("Start").apply {
                            addActionListener {
                                val color = JColorChooser.showDialog(this, "Fill Start Color", Color.WHITE)
                                color?.let {
                                    events.onNext(Event(CUBES_COLOR_FILL_START, it))
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
                                    events.onNext(Event(CUBES_COLOR_FILL_END, it))
                                    @Suppress("LABEL_NAME_CLASH")
                                    this@apply.background = it
                                }
                            }
                            isOpaque = true
                        })
                        add(JToggleButton("Fill")
                            .setup { ae -> events.onNext(Event(CUBES_FILL, isSelected(ae))) })
                        add(
                            JSlider(0, 255)
                                .setup(0, 1, 64, false) {
                                    val source = it.source as JSlider
                                    events.onNext(Event(CUBES_COLOR_FILL_ALPHA, source.value.toFloat()))
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
                                    events.onNext(Event(CUBES_COLOR_STROKE, it))
                                    @Suppress("LABEL_NAME_CLASH")
                                    this@apply.background = it
                                }
                            }
                            isOpaque = true
                        })
                        add(JToggleButton("Stroke")
                            .setup { ae -> events.onNext(Event(CUBES_STROKE, isSelected(ae))) })
                        add(
                            JSlider(0, 20)
                                .setup(LineShader.DEFAULT_WEIGHT.toInt(), 1, 5, false) {
                                    val source = it.source as JSlider
                                    events.onNext(Event(CUBES_STROKE_WEIGHT, source.value.toFloat()))
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
                                .setup { ae ->
                                    events.onNext(
                                        Event(
                                            TEXT_ORDER_RANDOM,
                                            isSelectedDeselectOthers(ae)
                                        )
                                    )
                                })
                            add(JToggleButton("Near Random")
                                .setup { ae ->
                                    events.onNext(
                                        Event(
                                            TEXT_ORDER_NEAR_RANDOM,
                                            isSelectedDeselectOthers(ae)
                                        )
                                    )
                                })
                            add(JToggleButton("In order")
                                .setup { ae ->
                                    events.onNext(
                                        Event(
                                            TEXT_ORDER_INORDER,
                                            isSelectedDeselectOthers(ae)
                                        )
                                    )
                                })
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
                                        // listener.textFont(dialog.selectedFont)
                                        events.onNext(Event(TEXT_FONT, dialog.selectedFont))
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
                                .setup { ae ->
                                    events.onNext(
                                        Event(
                                            TEXT_MOTION_CUBE,
                                            isSelectedDeselectOthers(ae)
                                        )
                                    )
                                })
                            add(JToggleButton("Around")
                                .setup { ae ->
                                    events.onNext(
                                        Event(
                                            TEXT_MOTION_AROUND,
                                            isSelectedDeselectOthers(ae)
                                        )
                                    )
                                })
                            add(JToggleButton("Fade")
                                .setup { ae ->
                                    events.onNext(
                                        Event(
                                            TEXT_MOTION_FADE,
                                            isSelectedDeselectOthers(ae)
                                        )
                                    )
                                })
                        }.wrapWithLabel("Motion")
                    )

                    // fill
                    add(JPanel().apply {
                        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
                        add(JButton("Start").apply {
                            addActionListener {
                                val color = JColorChooser.showDialog(this, "Fill Start Color", Color.WHITE)
                                color?.let {
                                    //listener.textFillColor(it)
                                    events.onNext(Event(TEXT_COLOR_FILL, it))
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
                                    events.onNext(Event(TEXT_COLOR_FILL_END, it))
                                    @Suppress("LABEL_NAME_CLASH")
                                    this@apply.background = it
                                }
                            }
                            isOpaque = true
                        })
                        add(JToggleButton("Fill")
                            .setup { ae -> events.onNext(Event(TEXT_FILL, isSelected(ae))) })
                        add(
                            JSlider(0, 255)
                                .setup(0, 1, 64, false) {
                                    val source = it.source as JSlider
                                    events.onNext(Event(TEXT_FILL_ALPHA, source.value.toFloat()))
                                    //listener.textFillAlpha(source.value.toFloat())
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
                                    //listener.textStrokeColor(it)
                                    events.onNext(Event(TEXT_COLOR_STROKE, it))
                                    @Suppress("LABEL_NAME_CLASH")
                                    this@apply.background = it
                                }
                            }
                            isOpaque = true
                        })
                        add(JToggleButton("Stroke")
                            .setup { ae -> events.onNext(Event(TEXT_STROKE, isSelected(ae))) })
                        add(
                            JSlider(0, 20)
                                .setup(LineShader.DEFAULT_WEIGHT.toInt(), 1, 5, false) {
                                    val source = it.source as JSlider
                                    events.onNext(Event(TEXT_STROKE_WEIGHT, source.value.toFloat()))
                                    //listener.textStrokeWeight(source.value.toFloat())
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

    private fun makeMenu(): JMenuBar {
        val menuBar = JMenuBar()

        //create menus
        val stateMenu = JMenu("State")
        stateMenu.mnemonic = KeyEvent.VK_F
        //create menu items
        val openStateMenuItem = JMenuItem("Open State")
        openStateMenuItem.mnemonic = KeyEvent.VK_O
        //openStateMenuItem.icon("baseline_movie_black_18.png")
        openStateMenuItem.actionCommand = "Open"
        openStateMenuItem.addActionListener {
            showOpenDialog("Open state", stateFile) {
                stateFile = it
                events.onNext(Event(MENU_OPEN_STATE, it))
            }
        }
        stateMenu.add(openStateMenuItem)

        val saveStateMenuItem = JMenuItem("Save State")
        saveStateMenuItem.mnemonic = KeyEvent.VK_S
        //saveStateMenuItem.icon("baseline_movie_black_18.png")
        saveStateMenuItem.actionCommand = "Save"
        saveStateMenuItem.addActionListener {
            showSaveDialog("Save state", stateFile) {
                stateFile = it
                events.onNext(Event(MENU_SAVE_STATE, it))
            }
        }
        stateMenu.add(saveStateMenuItem)

        stateMenu.addSeparator()

        val refreshMenuItem = JMenuItem("Refresh")
        refreshMenuItem.mnemonic = KeyEvent.VK_S
        //saveStateMenuItem.icon("baseline_movie_black_18.png")
        refreshMenuItem.actionCommand = "Refresh"
        refreshMenuItem.addActionListener {
            refreshFiles()
        }
        stateMenu.add(refreshMenuItem)
        menuBar.add(stateMenu)

        val textMenu = JMenu("Text")
        textMenu.mnemonic = KeyEvent.VK_F
        //create menu items
        val openTextMenuItem = JMenuItem("Open Text")
//        openTextMenuItem.mnemonic = KeyEvent.VK_O
        //openTextMenuItem.icon("baseline_movie_black_18.png")
        openTextMenuItem.actionCommand = "Open"
        openTextMenuItem.addActionListener {
            showOpenDialog("Open text", textDir) {
                textFile = it
                events.onNext(Event(MENU_OPEN_TEXT, it))
            }
        }
        textMenu.add(openTextMenuItem)
        val saveTextMenuItem = JMenuItem("Save Text")
//        saveTextMenuItem.mnemonic = KeyEvent.VK_S
        //saveTextMenuItem.icon("baseline_movie_black_18.png")
        saveTextMenuItem.actionCommand = "Save"
        saveTextMenuItem.addActionListener {
            showSaveDialog("Save text", textDir) {
                textFile = it
                events.onNext(Event(MENU_OPEN_TEXT, it))
            }
        }
        textMenu.add(saveTextMenuItem)
        menuBar.add(textMenu)
        return menuBar
    }

    fun refreshFiles() {
        val stateFiles = stateDir.listFiles()?.toList() ?: listOf()
        controlPanel.stateList.setData(stateFiles)

        val textFiles = textDir.listFiles()?.toList() ?: listOf()
        controlPanel.textList.setData(textFiles)
    }
}
