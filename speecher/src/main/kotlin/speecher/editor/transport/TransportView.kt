package speecher.editor.transport

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.koin.core.context.startKoin
import speecher.di.Modules
import speecher.editor.transport.TransportContract.*
import speecher.editor.transport.TransportContract.UiDataType.*
import speecher.editor.transport.TransportContract.UiEventType.*
import speecher.ui.util.*
import java.awt.*
import java.awt.event.*
import javax.swing.*
import javax.swing.border.BevelBorder
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder


fun main() {
    startKoin { modules(Modules.allModules) }
    TransportPresenter().apply {
        listener = object : TransportContract.StateListener {
            override fun speed(speed: Float) {
                println("StateListener speed = $speed")
            }
        }
        showWindow()
        Thread.sleep(200)
        setStatus("some status")
    }
}

class TransportView(
    private val presenter: TransportContract.Presenter
) : TransportContract.View {

    private lateinit var controlPanel: TransportPanel
    override val events: Subject<UiEvent> = BehaviorSubject.create()
    override lateinit var component: JComponent

    private val disposables: CompositeDisposable = CompositeDisposable()
    private val bgColor: Color = backgroundColor
    override fun showWindow() {
        SwingUtilities.invokeLater {
            val frame = JFrame("Transport")
            frame.defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
            frame.addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent) {
                    events.onNext(UiEvent(MENU_FILE_EXIT, null))
                }
            })

            controlPanel = TransportPanel()

            addMenu(frame)
            frame.add(controlPanel)
            frame.location = Point(300, 0)
            controlPanel.updateUI()

            disposables.add(
                presenter.updateObservable.subscribe({
                    when (it.uiDataType) {
                        MODE_PLAYING -> {
                            controlPanel.playButton.isVisible = false
                            controlPanel.pauseButton.isVisible = true
                        }
                        MODE_PAUSED -> {
                            controlPanel.playButton.isVisible = true
                            controlPanel.pauseButton.isVisible = false
                        }
//                        MUTED -> {
//                            val muted = it.data as Boolean
//                            println("Muted: $muted")
//                            controlPanel.muteButton.isSelected = muted
//                            controlPanel.muteButton.text = if (muted) {
//                                "Muted"
//                            } else {
//                                "Mute"
//                            }
//                        }
                        VOLUME -> controlPanel.volumeSlider.value =
                            (it.data as Float * controlPanel.volumeSlider.maximum).toInt()
                        SPEED -> controlPanel.speedLabel.text = "x ${it.data as Float}"
                        POSITION -> {
                            controlPanel.positionLabel.text = it.data as String
                        }
                        POSITION_SLIDER -> {
                            val listener = controlPanel.positionSlider.changeListeners[0]
                            controlPanel.positionSlider.removeChangeListener(listener)
                            controlPanel.positionSlider.value =
                                (it.data as Float * controlPanel.positionSlider.maximum).toInt()
                            controlPanel.positionSlider.addChangeListener(listener)
                        }
                        DURATION -> controlPanel.durationLabel.text = it.data as String
                        TITLE -> controlPanel.titleMovieLabel.text = it.data as String
                        READ_SRT -> controlPanel.titleSrtReadLabel.text = it.data as String
                        WRITE_SRT -> controlPanel.titleSrtWriteLabel.text = it.data as String
                        UiDataType.LOOP -> controlPanel.loopButton.isSelected = it.data as Boolean
                        else -> println("Not implemented : ${it.uiDataType}")
                    }
                }, {
                    println("error: ${it.localizedMessage}")
                    it.printStackTrace()
                })
            )

            // Display the window.
            frame.pack()
            frame.isVisible = true

            component = frame.rootPane
        }
    }

    override fun setStatus(status: String) {
        controlPanel.statusBar.text = status
    }

    override fun clearStatus() {
        controlPanel.statusBar.text = ""
    }

    inner class TransportPanel : JPanel() {
        val speedLabel: JLabel
        val titleMovieLabel: JLabel
        val titleSrtReadLabel: JLabel
        val titleSrtWriteLabel: JLabel
        val playButton: JButton
        val pauseButton: JButton
        val loopButton: JToggleButton
        val muteButton: JToggleButton
        val positionLabel: JLabel
        val durationLabel: JLabel
        val volumeSlider: JSlider
        val positionSlider: JSlider
        val statusBar: JLabel

        var positionSliderDragValue: Float? = null

        init {
            preferredSize = Dimension(1024, 280)
            layout = BorderLayout()

            add(JPanel().apply {
                layout = GridLayout(-1, 1)
                titledBorder("PLAY CONTROLS")
                background = bgColor
                // east panel - shader
                add(JPanel().apply {
                    preferredSize = Dimension(900, 80)
                    layout = BoxLayout(this, BoxLayout.LINE_AXIS)
                    background = bgColor
                    JButton()
                        .style()
                        .icon("baseline_fast_rewind_black_18.png")
                        .setup { events.onNext(UiEvent(REW)) }
                        .also { add(it) }
                    //add(JButton("|<").setup { events.onNext(UiEvent(LAST)) })
                    playButton = JButton()
                        .style()
                        .icon("baseline_play_arrow_black_18.png")
                        .setup { events.onNext(UiEvent(PLAY)) }
                        .also { add(it) }
                    pauseButton = JButton()
                        .style()
                        .icon("baseline_pause_black_18.png")
                        .setup { events.onNext(UiEvent(PAUSE)) }
                        .also { add(it) }
                        .also { it.isVisible = false }
                    JButton()
                        .style()
                        .icon("baseline_fast_forward_black_18.png")
                        .setup { events.onNext(UiEvent(FWD)) }
                        .also { add(it) }
                    //add(JButton(">|").setup { events.onNext(UiEvent(NEXT)) })

                    loopButton = JToggleButton("loop")
                        .style()
                        .icon("baseline_loop_black_18.png")
                        .setup { events.onNext(UiEvent(UiEventType.LOOP, isSelected(it))) }
                        .also { add(it) }

                    speedLabel = JLabel("x 1")
                        .style()
                        .icon("baseline_speed_black_18.png")
                        .also { add(it) }
                })

                positionSlider = JSlider(0, 1E6.toInt())
                    .setup(0, -1, -1, false) {
                        val source = it.source as JSlider
                        positionSliderDragValue = source.value.toFloat() / source.maximum
                        if (source.getValueIsAdjusting()) {
                            events.onNext(UiEvent(SEEK_DRAG, positionSliderDragValue))
                        } else {
                            events.onNext(
                                UiEvent(
                                    SEEK,
                                    positionSliderDragValue ?: source.value.toFloat() / source.maximum
                                )
                            )
                            positionSliderDragValue = null
                        }
                    }
                    .also { add(it.wrapWithLabel("Position")) }

                add(JPanel().apply {
                    preferredSize = Dimension(900, 80)
                    layout = BorderLayout()
                    background = bgColor
                    positionLabel = JLabel("00:00:00.000")
                        .style()
                        .also { add(it, BorderLayout.WEST) }
                    durationLabel = JLabel("00:00:00.000")
                        .style()
                        .also { add(it, BorderLayout.EAST) }
                })
                titleMovieLabel = JLabel("Title")
                    .style()
                    .also { add(it.wrapWithLabel("Movie", iconName = "baseline_movie_black_18.png")) }

                titleSrtReadLabel = JLabel("Read SRT")
                    .style()
                    .also { add(it.wrapWithLabel("Read SRT", iconName = "baseline_subtitles_black_18.png")) }

                titleSrtWriteLabel = JLabel("Write SRT")
                    .style()
                    .also { add(it.wrapWithLabel("Write SRT", iconName = "baseline_subtitles_black_18.png")) }

            }, BorderLayout.CENTER)

            JPanel().apply {
                layout = BorderLayout()
                titledBorder("VOLUME")
                background = bgColor
                JPanel().apply {
                    layout = GridLayout(1, -1)
                    background = bgColor
                    volumeSlider = JSlider(JSlider.VERTICAL, 0, 100, 100)
                        .setup(null, -1, -1, false) {
                            val source = it.source as JSlider
                            source.preferredSize = Dimension(20, 200)
                            events.onNext(UiEvent(VOLUME_CHANGED, source.value.toFloat() / source.maximum))
                        }.also { add(it);it.orientation = JSlider.VERTICAL }
                    JPanel().apply {
                        layout = BorderLayout()
                        background = bgColor

                        JLabel().icon("baseline_volume_up_black_18.png").also { add(it, BorderLayout.NORTH) }
                        JLabel().icon("baseline_volume_down_black_18.png").also { add(it, BorderLayout.SOUTH) }
                    }.also { add(it) }
                }.also { add(it, BorderLayout.CENTER); }

                muteButton = JToggleButton("Mute")
                    .style()
                    .icon("baseline_volume_off_black_18.png")
                    .setup { events.onNext(UiEvent(MUTE, isSelected(it))) }
                    .also { add(it, BorderLayout.SOUTH) }

            }.also { add(it, BorderLayout.EAST) }

            statusBar = JLabel()
                .style()
                .also {
                    it.preferredSize = Dimension(1024, 30)
                    background = bgColor
                    it.border = CompoundBorder(
                        BevelBorder(BevelBorder.RAISED, Color.decode("#cccccc"), Color.decode("#888888")),
                        EmptyBorder(5, 5, 5, 5)
                    )
                    it.foreground = Color.RED
                    add(it, BorderLayout.SOUTH)
                }
        }

    }

    private fun addMenu(mainFrame: JFrame) {
        //create a menu bar
        val menuBar = JMenuBar()

        //create menus
        val fileMenu = JMenu("File")
        fileMenu.mnemonic = KeyEvent.VK_F
        //fileMenu.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.META_DOWN_MASK)
        val editMenu = JMenu("Edit")
        editMenu.mnemonic = KeyEvent.VK_E
        val viewMenu = JMenu("View")
        editMenu.mnemonic = KeyEvent.VK_V
        //fileMenu.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.META_DOWN_MASK)
        //create menu items
        val openMovieMenuItem = JMenuItem("Open Movie")
        openMovieMenuItem.mnemonic = KeyEvent.VK_M
        openMovieMenuItem.icon("baseline_movie_black_18.png")
        openMovieMenuItem.actionCommand = "Open"

        val openReadSrtMenuItem = JMenuItem("Open SRT Read")
        openReadSrtMenuItem.mnemonic = KeyEvent.VK_O
        openReadSrtMenuItem.icon("baseline_subtitles_black_18.png")
        openMovieMenuItem.actionCommand = "Open"

        val newSrtMenuItem = JMenuItem("New SRT Words")
        newSrtMenuItem.mnemonic = KeyEvent.VK_N
        newSrtMenuItem.icon("baseline_fiber_new_black_18.png")
        newSrtMenuItem.actionCommand = "New"

        val openWriteSrtMenuItem = JMenuItem("Open SRT Words")
        openWriteSrtMenuItem.mnemonic = KeyEvent.VK_W
        openWriteSrtMenuItem.icon("baseline_subtitles_black_18.png")
        openWriteSrtMenuItem.actionCommand = "Open"

        val saveSrtMenuItem = JMenuItem("Save SRT Words")
        saveSrtMenuItem.mnemonic = KeyEvent.VK_S
        saveSrtMenuItem.icon("baseline_save_black_18.png")
        saveSrtMenuItem.actionCommand = "Save"

        val saveAsSrtMenuItem = JMenuItem("Save SRT Words As ...")
        saveAsSrtMenuItem.mnemonic = KeyEvent.VK_A
        saveAsSrtMenuItem.icon("content-save-as_black_18.png")
        saveAsSrtMenuItem.actionCommand = "Save As ..."

        val exitMenuItem = JMenuItem("Exit")
        exitMenuItem.mnemonic = KeyEvent.VK_X
        exitMenuItem.icon("baseline_exit_to_app_black_18.png")
        exitMenuItem.actionCommand = "Exit"

        val cutMenuItem = JMenuItem("Cut")
        cutMenuItem.mnemonic = KeyEvent.VK_X
        cutMenuItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK)
        cutMenuItem.icon("baseline_content_cut_black_18.png")
        cutMenuItem.actionCommand = "Cut"

        val copyMenuItem = JMenuItem("Copy")
        copyMenuItem.mnemonic = KeyEvent.VK_C
        copyMenuItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK)
        copyMenuItem.icon("baseline_content_copy_black_18.png")
        copyMenuItem.actionCommand = "Copy"

        val pasteMenuItem = JMenuItem("Paste")
        pasteMenuItem.mnemonic = KeyEvent.VK_V
        pasteMenuItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK)
        pasteMenuItem.icon("baseline_content_paste_black_18.png")
        pasteMenuItem.actionCommand = "Paste"

        val showReadSrtMenuItem = JMenuItem("Read Subtitles")
        showReadSrtMenuItem.mnemonic = KeyEvent.VK_R
        showReadSrtMenuItem.icon("baseline_list_black_18.png")
        showReadSrtMenuItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.META_DOWN_MASK)

        val showWriteSrtMenuItem = JMenuItem("Write Subtitles")
        showWriteSrtMenuItem.mnemonic = KeyEvent.VK_W
        showWriteSrtMenuItem.icon("baseline_list_black_18.png")
        showWriteSrtMenuItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.META_DOWN_MASK)

        val editSrtMenuItem = JMenuItem("Edit Subtitles")
        editSrtMenuItem.mnemonic = KeyEvent.VK_E
        editSrtMenuItem.icon("baseline_edit_black_18.png")
        editSrtMenuItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.META_DOWN_MASK)

        // val sysMenuItemListener = SysOutMenuItemListener()

        openMovieMenuItem.addActionListener(EventMenuItemListener(MENU_FILE_OPEN_MOVIE))
        newSrtMenuItem.addActionListener(EventMenuItemListener(MENU_FILE_NEW_SRT_WRITE))
        openReadSrtMenuItem.addActionListener(EventMenuItemListener(MENU_FILE_OPEN_SRT_READ))
        openWriteSrtMenuItem.addActionListener(EventMenuItemListener(MENU_FILE_OPEN_SRT_WRITE))
        saveSrtMenuItem.addActionListener(EventMenuItemListener(MENU_FILE_SAVE_SRT))
        saveAsSrtMenuItem.addActionListener(EventMenuItemListener(MENU_FILE_SAVE_SRT_AS))
        exitMenuItem.addActionListener(EventMenuItemListener(MENU_FILE_EXIT))

        cutMenuItem.addActionListener(EventMenuItemListener(MENU_EDIT_CUT))
        copyMenuItem.addActionListener(EventMenuItemListener(MENU_EDIT_COPY))
        pasteMenuItem.addActionListener(EventMenuItemListener(MENU_EDIT_PASTE))

        showReadSrtMenuItem.addActionListener(EventMenuItemListener(MENU_VIEW_READ_SUBLIST))
        showWriteSrtMenuItem.addActionListener(EventMenuItemListener(MENU_VIEW_WRITE_SUBLIST))
        editSrtMenuItem.addActionListener(EventMenuItemListener(MENU_VIEW_EDIT_SUBLIST))

        //add menu items to menus
        fileMenu.add(openMovieMenuItem.style())
        fileMenu.addSeparator()
        fileMenu.add(openReadSrtMenuItem.style())
        fileMenu.addSeparator()
        fileMenu.add(newSrtMenuItem.style())
        fileMenu.add(openWriteSrtMenuItem.style())
        fileMenu.add(saveSrtMenuItem.style())
        fileMenu.add(saveAsSrtMenuItem.style())
        fileMenu.addSeparator()
        fileMenu.add(exitMenuItem.style())

        editMenu.add(cutMenuItem.style())
        editMenu.add(copyMenuItem.style())
        editMenu.add(pasteMenuItem.style())

        viewMenu.add(showReadSrtMenuItem.style())
        viewMenu.add(showWriteSrtMenuItem.style())
        viewMenu.add(editSrtMenuItem.style())

        //add menu to menubar
        menuBar.add(fileMenu.style())
        menuBar.add(editMenu.style())
        menuBar.add(viewMenu.style())

        //add menubar to the frame
        mainFrame.setJMenuBar(menuBar)
    }

    @Suppress("unused")
    internal class SysOutMenuItemListener : ActionListener {
        override fun actionPerformed(e: ActionEvent) {
            println("${e.actionCommand} JMenuItem clicked.")
        }
    }

    inner class EventMenuItemListener constructor(
        private val ev: UiEventType
    ) : ActionListener {
        override fun actionPerformed(e: ActionEvent) {
            events.onNext(UiEvent(ev, null))
        }
    }

}