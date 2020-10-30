package speecher.editor.transport

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import speecher.editor.transport.TransportContract.UiDataType.*
import speecher.editor.transport.TransportContract.UiEvent
import speecher.editor.transport.TransportContract.UiEventType.*
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import javax.swing.*

fun main() {
    val view = TransportView()
    view.presenter = TransportPresenter(view, TransportState())
    view.showWindow()
}

class TransportView() : TransportContract.View {

    lateinit var presenter: TransportPresenter
    private lateinit var controlPanel: TransportPanel
    override val events: Subject<UiEvent> = BehaviorSubject.create()

    private val disposables: CompositeDisposable = CompositeDisposable()

    fun showWindow() {
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
                        SPEED -> {
                            controlPanel.speedLabel.text = "x ${it.data as Int}"
                        }
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
        }
    }

    inner class TransportPanel constructor() : JPanel() {
        val speedLabel: JLabel
        val titleLabel: JLabel
        val playButton: JButton
        val pauseButton: JButton
        val muteButton: JToggleButton

        init {
            preferredSize = Dimension(800, 150)
            layout = BorderLayout()

            add(JPanel().apply {
                layout = GridLayout(-1, 1)
                titledBorder("PLAY CONTROLS")
                // east panel - shader
                add(JPanel().apply {
                    preferredSize = Dimension(400, 80)
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

                add(JSlider(0, 1E6.toInt())
                    .setup(0, -1, -1, false) {
                        val source = it.source as JSlider
                        events.onNext(UiEvent(SEEK, source.value.toFloat() / source.maximum))
                    })

                add(JSlider(-1000000, 1000000)
                    .setup(0, -1, -1, false) {
                        val source = it.source as JSlider
                        events.onNext(UiEvent(FINE_SEEK, source.value.toFloat() / source.maximum))

                    })
                titleLabel = JLabel("Title")
                    .let { add(it); it }

            }, BorderLayout.CENTER)

            add(JPanel().apply {
                layout = GridLayout(-1, 1)
                titledBorder("VOLUME")

                add(JSlider(JSlider.VERTICAL, 0, 100, 100)
                    .setup(0, -1, -1, false) {
                        val source = it.source as JSlider
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
        //create a menu bar
        val menuBar = JMenuBar()

        //create menus

        //create menus
        val fileMenu = JMenu("File")
        val editMenu = JMenu("Edit")

        //create menu items
        val newMenuItem = JMenuItem("New")
        newMenuItem.setMnemonic(KeyEvent.VK_N)
        newMenuItem.actionCommand = "New"

        val openMenuItem = JMenuItem("Open")
        openMenuItem.actionCommand = "Open"

        val saveMenuItem = JMenuItem("Save")
        saveMenuItem.actionCommand = "Save"

        val exitMenuItem = JMenuItem("Exit")
        exitMenuItem.actionCommand = "Exit"

        val cutMenuItem = JMenuItem("Cut")
        cutMenuItem.actionCommand = "Cut"

        val copyMenuItem = JMenuItem("Copy")
        copyMenuItem.actionCommand = "Copy"

        val pasteMenuItem = JMenuItem("Paste")
        pasteMenuItem.actionCommand = "Paste"

        // val sysMenuItemListener = SysOutMenuItemListener()

        newMenuItem.addActionListener(EventMenuItemListener(MENU_FILE_NEW))
        openMenuItem.addActionListener(EventMenuItemListener(MENU_FILE_OPEN))
        saveMenuItem.addActionListener(EventMenuItemListener(MENU_FILE_SAVE))
        exitMenuItem.addActionListener(EventMenuItemListener(MENU_FILE_EXIT))

        cutMenuItem.addActionListener(EventMenuItemListener(MENU_EDIT_CUT))
        copyMenuItem.addActionListener(EventMenuItemListener(MENU_EDIT_COPY))
        pasteMenuItem.addActionListener(EventMenuItemListener(MENU_EDIT_PASTE))

        //add menu items to menus
        fileMenu.add(newMenuItem)
        fileMenu.add(openMenuItem)
        fileMenu.add(saveMenuItem)
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