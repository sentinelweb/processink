package cubes.gui


//Generated by GuiGenie - Copyright (c) 2004 Mario Awad.
//Home Page http://guigenie.cjb.net - Check often for new versions!

import cubes.CubesContract.*
import cubes.CubesContract.BackgroundShaderType.*
import cubes.CubesContract.Control.*
import cubes.CubesContract.Event
import cubes.CubesContract.Formation.*
import cubes.CubesContract.Model3D.MILLENIUM_FALCON
import cubes.CubesContract.Model3D.TERMINATOR
import cubes.CubesContract.TextTransition.*
import cubes.models.TextList
import cubes.shaders.LineShader
import cubes.util.wrapper.FilesWrapper
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.drjekyll.fontchooser.FontDialog
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import javax.swing.*

fun main() {
    val filesDir = FilesWrapper(File(System.getProperty("user.home"), "cubes"))
    Controls(filesDir)
        .showWindow()
}

/**
 * TODO make a model object to hold data
 */
class Controls(
    private var files: FilesWrapper
) {

    private lateinit var controlPanel: ControlsPanel
    private val events: Subject<Event> = BehaviorSubject.create()

    fun events(): Observable<Event> = events

    fun showWindow() {
        SwingUtilities.invokeLater {
            val frame = JFrame("Controls")
            frame.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
            frame.addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent) {
                    events.onNext(Event(MENU_EXIT, null))
                }
            })
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

    inner class ControlsPanel : JPanel() {
        var stateList: JList<File>
        var textList: JList<File>

        init {
            preferredSize = Dimension(1100, 880)
            layout = BorderLayout()

            // west panel - states/texts
            add(JPanel().apply {
                preferredSize = Dimension(200, 400)
                layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
                add(JPanel().apply {
                    titledBorder("State")
                    val stateFiles =
                        files.stateDir.listFiles()?.toList()
                            ?.filter { it.name.endsWith(".json") } ?: listOf()
                    JList<File>()
                        .also { stateList = it }
                        .setup(stateFiles) { events.onNext(Event(MENU_OPEN_STATE, it)) }
                        .also { add(it) }

                })
                add(JPanel().apply {
                    titledBorder("Text")
                    val textFiles =
                        files.textDir.listFiles()?.toList()
                            ?.filter { it.name.endsWith(".txt") } ?: listOf()
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
                titledBorder("Background")
                add(JButton("BG Color").apply {
                    addActionListener {
                        val color = JColorChooser.showDialog(this, "Background Color", Color.WHITE)
                        color?.let {
                            events.onNext(Event(BG_COLOR, it))

                            @Suppress("LABEL_NAME_CLASH")
                            this@apply.background = it
                        }
                    }
                })
                add(JButton("None").setup { events.onNext(Event(SHADER_BG, BackgroundShaderType.NONE)) })
                add(JButton("Fuji").setup { events.onNext(Event(SHADER_BG, FUJI)) })
                add(JButton("Deform").setup { events.onNext(Event(SHADER_BG, DEFORM)) })
                add(JButton("Monjori").setup { events.onNext(Event(SHADER_BG, MONJORI)) })
                add(JButton("Eclipse").setup { events.onNext(Event(SHADER_BG, ECLIPSE)) })
                add(JButton("OneWarp").setup { events.onNext(Event(SHADER_BG, ONEWARP)) })
                add(JButton("ProcWarp").setup { events.onNext(Event(SHADER_BG, PROCWARP)) })
                add(JButton("BurningStar").setup { events.onNext(Event(SHADER_BG, BURNING_STAR)) })
                add(JLabel("-- Non-colourised---"))
                add(JButton("Nebula").setup { events.onNext(Event(SHADER_BG, NEBULA)) })
                add(JButton("ColdFlame").setup { events.onNext(Event(SHADER_BG, COLDFLAME)) })
                add(JButton("Refraction").setup { events.onNext(Event(SHADER_BG, REFRACTION_PATTERN)) })
                add(JButton("Water").setup { events.onNext(Event(SHADER_BG, WATER)) })
                add(JButton("Fractal pyramid").setup { events.onNext(Event(SHADER_BG, FRACTAL_PYRAMID)) })
                add(JButton("Octagrams").setup { events.onNext(Event(SHADER_BG, OCTAGRAMS)) })
                add(JButton("Protean clouds").setup { events.onNext(Event(SHADER_BG, PROTEAN_CLOUDS)) })
                add(JButton("Clouds").setup { events.onNext(Event(SHADER_BG, CLOUDS)) })
                add(JButton("Hyperfield").setup { events.onNext(Event(SHADER_BG, HYPERFIELD)) })
                add(JButton("Starfield1").setup { events.onNext(Event(SHADER_BG, STARFIELD_1)) })
            }, BorderLayout.EAST)

            // center panel - motion, text
            add(JPanel().apply {
                layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
                add(JPanel().apply {
                    layout = GridLayout(-1, 1)
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
                // cubes panel
                add(JPanel().apply {
                    layout = GridLayout(-1, 1)
                    titledBorder("Cubes")
                    // rotation
                    add(
                        JPanel().apply {
                            layout = BoxLayout(this, BoxLayout.X_AXIS)
                            add(JToggleButton("Visible")
                                .setup(true) { ae -> events.onNext(Event(CUBES_VISIBLE, isSelected(ae))) })
                            add(JLabel("|"))
                            add(JToggleButton("X")
                                .setup(true) { ae ->
                                    events.onNext(
                                        Event(CUBES_ROTATION, Pair(RotationAxis.X, isSelected(ae)))
                                    )
                                })
                            add(JToggleButton("Y")
                                .setup(true) { ae ->
                                    events.onNext(
                                        Event(CUBES_ROTATION, Pair(RotationAxis.Y, isSelected(ae)))
                                    )
                                })
                            add(JToggleButton("Z")
                                .setup(true) { ae ->
                                    events.onNext(
                                        Event(CUBES_ROTATION, Pair(RotationAxis.Z, isSelected(ae)))
                                    )
                                })
                            add(JLabel("|"))
                            add(JButton("0")
                                .setup { events.onNext(Event(CUBES_ROTATION_RESET)) })
                            add(JButton("Align").setup { events.onNext(Event(CUBES_ROTATION_ALIGN)) })
                        }
                            .wrapWithLabel("Rotation", 100))

                    // translation
                    add(
                        JPanel().apply {
                            layout = BoxLayout(this, BoxLayout.X_AXIS)
                            add(JButton("grid")
                                .setup { events.onNext(Event(CUBES_FORMATION, GRID)) })
                            add(JButton("line")
                                .setup { events.onNext(Event(CUBES_FORMATION, LINE)) })
                            add(JButton("square")
                                .setup { events.onNext(Event(CUBES_FORMATION, SQUARE)) })
                            add(JButton("0")
                                .setup { events.onNext(Event(CUBES_FORMATION, CENTER)) })
                        }.wrapWithLabel("Formation", 100)
                    )

                    // speed
                    add(
                        JPanel().apply {
                            layout = BoxLayout(this, BoxLayout.X_AXIS)
                            add(
                                JSlider(-400, 400)
                                    .setup(0, 1, 200, false) {
                                        val source = it.source as JSlider
                                        events.onNext(Event(CUBES_ROTATION_SPEED, source.value.toFloat()))
                                    })
                            add(JButton("0ffset").setup { events.onNext(Event(CUBES_ROTATION_OFFEST_RESET)) })
                            add(
                                JSlider(-100, 100)
                                    .setup(0, 1, 50, false) {
                                        val source = it.source as JSlider
                                        events.onNext(Event(CUBES_ROTATION_OFFEST_SPEED, source.value.toFloat()))
                                    }
                                    .apply { value = 1 }
                            )
                        }.wrapWithLabel("Speed", 100)
                    )

                    // scale
                    add(
                        JPanel().apply {
                            layout = BoxLayout(this, BoxLayout.X_AXIS)

                            add(
                                JSlider(0, 400)
                                    .setup(0, 1, 200, false) {
                                        val source = it.source as JSlider
                                        events.onNext(Event(CUBES_SCALE_BASE, source.value.toFloat()))
                                    }
                            )
                            add(
                                JSlider(0, 400)
                                    .setup(0, 1, 200, false) {
                                        val source = it.source as JSlider
                                        events.onNext(Event(CUBES_SCALE_OFFSET, source.value.toFloat()))
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
                                .apply { value = 3000 }
                                .setup(0, 1, 64, false) {
                                    val source = it.source as JSlider
                                    events.onNext(Event(CUBES_COLOR_FILL_ALPHA, source.value))
                                }
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
                            add(JToggleButton("Visible")
                                .setup { ae -> events.onNext(Event(TEXT_VISIBLE, isSelected(ae))) })
                            add(JLabel("|"))
                            add(JButton("Random")
                                .setup { ae -> events.onNext(Event(TEXT_ORDER, TextList.Ordering.RANDOM)) })
                            add(JButton("Near Random")
                                .setup { ae -> events.onNext(Event(TEXT_ORDER, TextList.Ordering.NEAR_RANDOM)) })
                            add(JButton("In order")
                                .setup { ae -> events.onNext(Event(TEXT_ORDER, TextList.Ordering.INORDER)) })
                            add(JLabel("|"))
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
                            add(JButton("None").setup { ae -> events.onNext(Event(TEXT_MOTION, TextTransition.NONE)) })
                            add(JButton("Fade").setup { ae -> events.onNext(Event(TEXT_MOTION, FADE)) })
                            add(JButton("Fade-Zoom").setup { ae -> events.onNext(Event(TEXT_MOTION, FADE_ZOOM)) })
                            add(JButton("Spin X").setup { ae -> events.onNext(Event(TEXT_MOTION, SPIN_X)) })
                            add(JButton("Spin Y").setup { ae -> events.onNext(Event(TEXT_MOTION, SPIN_Y)) })
                        }.wrapWithLabel("Motion")
                    )

                    // fill
                    add(JPanel().apply {
                        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
                        add(JButton("Color").apply {
                            addActionListener {
                                val color = JColorChooser.showDialog(this, "Fill Start Color", Color.WHITE)
                                color?.let {
                                    events.onNext(Event(TEXT_COLOR_FILL, it))
                                    @Suppress("LABEL_NAME_CLASH")
                                    this@apply.background = it
                                }
                            }
                            isOpaque = true
                        })
                        add(
                            JSlider(0, 255)
                                .apply { value = 255 }
                                .setup(0, 1, 64, false) {
                                    val source = it.source as JSlider
                                    events.onNext(Event(TEXT_FILL_ALPHA, source.value))
                                }
                                .wrapWithLabel("Alpha")
                        )
                    }.wrapWithLabel("Fill"))

                    // text
                    add(JPanel().apply {
                        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
                        val textSetField = JTextField().also { add(it) }
                        add(JButton("Apply")
                            .apply {
                                addActionListener {
                                    events.onNext(Event(TEXT_SET, textSetField.text.split(":")))
                                }
                            })
                        add(JButton("Restart")
                            .apply { addActionListener { events.onNext(Event(TEXT_GOTO, 0)) } })
                        add(JButton("Next")
                            .apply { addActionListener { events.onNext(Event(TEXT_NEXT, null)) } })
                    }.wrapWithLabel("Text"))
                })
                add(JPanel().apply {
                    layout = GridLayout(-1, 1)
                    titledBorder("Objects")

                    add(JPanel().apply {
                        layout = BoxLayout(this, BoxLayout.X_AXIS)
                        add(JToggleButton("Terminator").setup { ae -> addOrRemoveModel(ae, TERMINATOR) })
                        add(JToggleButton("MF").setup { ae -> addOrRemoveModel(ae, MILLENIUM_FALCON) })
                        add(JLabel("|"))
                        add(JToggleButton("Buddha").setup { ae -> addOrRemoveImage(ae, "buddha.svg") })
                        add(JToggleButton("Yin Yang").setup { ae -> addOrRemoveImage(ae, "yinyang.svg") })
                        add(JToggleButton("Hand").setup { ae -> addOrRemoveImage(ae, "buddhism_hand2.svg") })

                    }, BorderLayout.CENTER)
                })
                add(JPanel().apply {
                    layout = GridLayout(-1, 1)
                    titledBorder("Particles")

                    add(JPanel().apply {
                        layout = BoxLayout(this, BoxLayout.X_AXIS)
                        add(JButton("Trigger")
                            .apply { addActionListener { events.onNext(Event(PARTICLE_SYS_CREATE, 0)) } })
                        add(JButton("Fill").apply {
                            addActionListener {
                                val color = JColorChooser.showDialog(this, "Fill Color", Color.WHITE)
                                color.let {
                                    events.onNext(Event(PARTICLE_FILL_COLOUR, it))
                                    @Suppress("LABEL_NAME_CLASH")
                                    this@apply.background = it
                                }
                            }
                            isOpaque = true
                        })
                        add(JButton("Stroke").apply {
                            addActionListener {
                                val color = JColorChooser.showDialog(this, "Stroke Color", Color.WHITE)
                                color.let {
                                    events.onNext(Event(PARTICLE_STROKE_COLOUR, it))
                                    @Suppress("LABEL_NAME_CLASH")
                                    this@apply.background = it
                                }
                            }
                            isOpaque = true
                        })
                        add(JLabel("#"))
                        add(JSpinner().apply {
                            addChangeListener { events.onNext(Event(PARTICLE_NUMBER, this.value)) }
                            value = 50
                        })
                        add(JLabel("Life"))
                        add(JSpinner().apply {
                            addChangeListener { events.onNext(Event(PARTICLE_LIFESPAN, this.value)) }
                            value = 1000
                        })
                        add(JLabel("Sz"))
                        add(JSpinner().apply {
                            addChangeListener { events.onNext(Event(PARTICLE_SIZE, this.value)) }
                            value = 1
                        })
                        val textSetField = JTextField()
                        add(JSpinner(SpinnerListModel(ParticleShape.values())).apply {
                            addChangeListener {
                                events.onNext(Event(PARTICLE_SHAPE, this.value))
                                if (this.value == ParticleShape.SVG) {
                                    events.onNext(Event(PARTICLE_SHAPE_PATH, textSetField.text.ifBlank { null }))
                                }
                            }
                            minimumSize = Dimension(200, 40)
                        })
                        textSetField.also { add(it) }

                    }, BorderLayout.CENTER)
                })
            }, BorderLayout.CENTER)
        }

        private fun addOrRemoveModel(ae: ActionEvent, data: Model3D) {
            if (isSelected(ae)) {
                events.onNext(Event(ADD_MODEL, data))
            } else {
                events.onNext(Event(REMOVE_MODEL, data))
            }
        }

        private fun addOrRemoveImage(ae: ActionEvent, data: String) {
            if (isSelected(ae)) {
                events.onNext(Event(ADD_IMAGE, data))
            } else {
                events.onNext(Event(REMOVE_IMAGE, data))
            }
        }

        private fun isSelected(ae: ActionEvent) = (ae.source as JToggleButton).isSelected
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
            showOpenDialog("Open state", files.stateDir) {
                events.onNext(Event(MENU_OPEN_STATE, it))
            }
        }
        stateMenu.add(openStateMenuItem)

        val saveStateMenuItem = JMenuItem("Save State")
        saveStateMenuItem.mnemonic = KeyEvent.VK_S
        //saveStateMenuItem.icon("baseline_movie_black_18.png")
        saveStateMenuItem.actionCommand = "Save"
        saveStateMenuItem.addActionListener {
            showSaveDialog("Save state", files.stateDir) {
                val file =
                    if (it.name.endsWith(".json")) it
                    else {
                        File(it.absolutePath + ".json")
                    }
                events.onNext(Event(MENU_SAVE_STATE, file))
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
            showOpenDialog("Open text", files.textDir) {
                if (it.name.endsWith(".txt")) {
                    events.onNext(Event(MENU_OPEN_TEXT, it))
                } else println("Not a valid text file")
            }
        }
        textMenu.add(openTextMenuItem)
        menuBar.add(textMenu)
        return menuBar
    }

    fun refreshFiles() {
        val stateFiles = files.stateDir.listFiles()?.toList()?.filter { it.name.endsWith(".json") } ?: listOf()
        controlPanel.stateList.setData(stateFiles)

        val textFiles = files.textDir.listFiles()?.toList()?.filter { it.name.endsWith(".txt") } ?: listOf()
        controlPanel.textList.setData(textFiles)
    }
}
