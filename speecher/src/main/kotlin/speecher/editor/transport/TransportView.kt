package speecher.editor.transport

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import speecher.editor.transport.TransportContract.UiDataType.*
import speecher.editor.transport.TransportContract.UiEvent
import speecher.editor.transport.TransportContract.UiEventType.*
import speecher.util.format.TimeFormatter
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import javax.swing.*

fun main() {
    val view = TransportView()
    TransportPresenter(view, TransportState(), TimeFormatter())
}

class TransportView() : TransportContract.View {

    override lateinit var presenter: TransportPresenter
    private lateinit var controlPanel: TransportPanel
    override val events: Subject<UiEvent> = BehaviorSubject.create()
    override lateinit var component: JComponent

    private val disposables: CompositeDisposable = CompositeDisposable()

    override fun showWindow() {
        SwingUtilities.invokeLater {
            val frame = JFrame("Controls")
            frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

            controlPanel = TransportPanel()

            addMenu(frame)
            frame.add(controlPanel)

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
                        MUTED -> {
                            val muted = it.data as Boolean
                            println("Muted: $muted")
                            controlPanel.muteButton.isSelected = muted
                            controlPanel.muteButton.text = if (muted) {
                                "Muted"
                            } else {
                                "Mute"
                            }
                        }
                        SPEED -> controlPanel.speedLabel.text = "x ${it.data as Float}"
                        POSITION -> controlPanel.positionLabel.text = it.data as String
                        DURATION -> controlPanel.durationLabel.text = it.data as String
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

    inner class TransportPanel constructor() : JPanel() {
        val speedLabel: JLabel
        val titleMovieLabel: JLabel
        val titleSrtReadLabel: JLabel
        val titleSrtWriteLabel: JLabel
        val playButton: JButton
        val pauseButton: JButton
        val muteButton: JToggleButton
        val positionLabel: JLabel
        val durationLabel: JLabel

        init {
            preferredSize = Dimension(1024, 250)
            layout = BorderLayout()

            add(JPanel().apply {
                layout = GridLayout(-1, 1)
                titledBorder("PLAY CONTROLS")
                // east panel - shader
                add(JPanel().apply {
                    preferredSize = Dimension(900, 80)
                    layout = BoxLayout(this, BoxLayout.LINE_AXIS)

                    add(JButton("|<").setup { events.onNext(UiEvent(LAST)) })
                    add(JButton("<<").setup { events.onNext(UiEvent(REW)) })
                    playButton = JButton(">")
                        .setup { events.onNext(UiEvent(PLAY)) }
                        .let { add(it); it }
                    pauseButton = JButton("||")
                        .setup { events.onNext(UiEvent(PAUSE)) }
                        .let { add(it); it }
                        .let { it.isVisible = false; it }
                    add(JButton(">>").setup { events.onNext(UiEvent(FWD)) })
                    add(JButton(">|").setup { events.onNext(UiEvent(NEXT)) })
                    speedLabel = JLabel("x 1")
                        .let { add(it); it }
                })

                add(
                    JSlider(0, 1E6.toInt())
                        .setup(0, -1, -1, false) {
                            val source = it.source as JSlider
                            events.onNext(UiEvent(SEEK, source.value.toFloat() / source.maximum))
                        }.wrapWithLabel("Position")
                )

                add(
                    JSlider(-1000000, 1000000)
                        .setup(0, -1, -1, false) {
                            val source = it.source as JSlider
                            events.onNext(UiEvent(FINE_SEEK, source.value.toFloat() / source.maximum))

                        }.wrapWithLabel("Fine")
                )

                add(JPanel().apply {
                    preferredSize = Dimension(900, 80)
                    layout = BorderLayout()
                    positionLabel = JLabel("00:00:00.000")
                        .let { add(it, BorderLayout.WEST); it }
                    durationLabel = JLabel("00:00:00.000")
                        .let { add(it, BorderLayout.EAST); it }
                })
                titleMovieLabel = JLabel("Title")
                    .let { add(it.wrapWithLabel("Movie")); it }

                titleSrtReadLabel = JLabel("Read SRT")
                    .let { add(it.wrapWithLabel("Read SRT")); it }

                titleSrtWriteLabel = JLabel("Write SRT")
                    .let { add(it.wrapWithLabel("Write SRT")); it }

            }, BorderLayout.CENTER)

            add(JPanel().apply {
                layout = GridLayout(1, -1)
                titledBorder("VOLUME")

                add(JSlider(JSlider.VERTICAL, 0, 100, 100)
                    .setup(0, -1, -1, false) {
                        val source = it.source as JSlider
                        source.orientation = JSlider.VERTICAL
                        //source.size = Dimension(20,200)
                        source.preferredSize = Dimension(20, 200)
                        events.onNext(UiEvent(VOLUME_CHANGED, source.value.toFloat() / source.maximum))
                    })

                muteButton = JToggleButton("Mute")
                    .setup {
                        val source = it.source as JToggleButton
                        events.onNext(UiEvent(MUTE, source.isSelected))
                    }
                    .let { add(it) } as JToggleButton

            }, BorderLayout.EAST)
        }

    }

    fun addMenu(mainFrame: JFrame) {
        //create a menu bar
        val menuBar = JMenuBar()

        //create menus
        val fileMenu = JMenu("File")
        fileMenu.mnemonic = KeyEvent.VK_F
        //fileMenu.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.META_DOWN_MASK)
        val editMenu = JMenu("Edit")
        editMenu.mnemonic = KeyEvent.VK_E
        //fileMenu.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.META_DOWN_MASK)
        //create menu items
        val openMovieMenuItem = JMenuItem("Open Movie")
        openMovieMenuItem.setMnemonic(KeyEvent.VK_M)
        openMovieMenuItem.actionCommand = "Open"

        val openReadSrtMenuItem = JMenuItem("Open SRT Read")
        openReadSrtMenuItem.setMnemonic(KeyEvent.VK_O)
        openMovieMenuItem.actionCommand = "Open"

        val newSrtMenuItem = JMenuItem("New SRT Write")
        newSrtMenuItem.setMnemonic(KeyEvent.VK_N)
        newSrtMenuItem.actionCommand = "New"

        val openWriteSrtMenuItem = JMenuItem("Open SRT Write")
        openWriteSrtMenuItem.setMnemonic(KeyEvent.VK_W)
        openMovieMenuItem.actionCommand = "Open"

        val saveSrtMenuItem = JMenuItem("Save SRT")
        saveSrtMenuItem.setMnemonic(KeyEvent.VK_S)
        saveSrtMenuItem.actionCommand = "Save"

        val exitMenuItem = JMenuItem("Exit")
        exitMenuItem.setMnemonic(KeyEvent.VK_X)
        exitMenuItem.actionCommand = "Exit"

        val cutMenuItem = JMenuItem("Cut")
        cutMenuItem.setMnemonic(KeyEvent.VK_X)
        cutMenuItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK)
        cutMenuItem.actionCommand = "Cut"

        val copyMenuItem = JMenuItem("Copy")
        copyMenuItem.setMnemonic(KeyEvent.VK_C)
        copyMenuItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK)
        copyMenuItem.actionCommand = "Copy"

        val pasteMenuItem = JMenuItem("Paste")
        pasteMenuItem.setMnemonic(KeyEvent.VK_V)
        pasteMenuItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK)
        pasteMenuItem.actionCommand = "Paste"

        // val sysMenuItemListener = SysOutMenuItemListener()

        openMovieMenuItem.addActionListener(EventMenuItemListener(MENU_FILE_OPEN_MOVIE))
        newSrtMenuItem.addActionListener(EventMenuItemListener(MENU_FILE_NEW_SRT_WRITE))
        openReadSrtMenuItem.addActionListener(EventMenuItemListener(MENU_FILE_OPEN_SRT_READ))
        openWriteSrtMenuItem.addActionListener(EventMenuItemListener(MENU_FILE_OPEN_SRT_WRITE))
        saveSrtMenuItem.addActionListener(EventMenuItemListener(MENU_FILE_SAVE_SRT))
        exitMenuItem.addActionListener(EventMenuItemListener(MENU_FILE_EXIT))

        cutMenuItem.addActionListener(EventMenuItemListener(MENU_EDIT_CUT))
        copyMenuItem.addActionListener(EventMenuItemListener(MENU_EDIT_COPY))
        pasteMenuItem.addActionListener(EventMenuItemListener(MENU_EDIT_PASTE))

        //add menu items to menus
        fileMenu.add(openMovieMenuItem)
        fileMenu.addSeparator()
        fileMenu.add(openReadSrtMenuItem)
        fileMenu.addSeparator()
        fileMenu.add(newSrtMenuItem)
        fileMenu.add(openWriteSrtMenuItem)
        fileMenu.add(saveSrtMenuItem)
        fileMenu.addSeparator()
        fileMenu.add(exitMenuItem)

        editMenu.add(cutMenuItem)
        editMenu.add(copyMenuItem)
        editMenu.add(pasteMenuItem)

        //add menu to menubar
        menuBar.add(fileMenu)
        menuBar.add(editMenu)

        //add menubar to the frame
        mainFrame.setJMenuBar(menuBar)
    }

    internal class SysOutMenuItemListener : ActionListener {
        override fun actionPerformed(e: ActionEvent) {
            println(e.getActionCommand().toString() + " JMenuItem clicked.")
        }
    }

    inner class EventMenuItemListener constructor(val ev: TransportContract.UiEventType) : ActionListener {
        override fun actionPerformed(e: ActionEvent) {
            events.onNext(UiEvent(ev, null))
        }
    }

}