package speecher.generator.ui

import speecher.domain.Subtitles
import speecher.editor.util.backgroundColor
import speecher.editor.util.style
import speecher.util.format.TimeFormatter
import java.awt.Color
import java.awt.GridLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.BevelBorder

class SubtitleChipView constructor(
    timeFormatter: TimeFormatter,
    item: Subtitles.Subtitle,
    private val listner: Listener
) : JPanel() {

    private val bgColor: Color = backgroundColor

    private val colorSelected = Color.decode("#cccccc")
    private val colorNormal = bgColor

    private val colorClick = Color.decode("#aaaaaa")
    private var mouseDown = false

    var selected = false
        set(value) {
            field = value
            setBackground()
        }

    init {
        layout = GridLayout(-1, 1)
        add(
            JLabel("${timeFormatter.formatTime(item.fromSec)} -> ${timeFormatter.formatTime(item.toSec)}s")
                .style(10)
        )
        item.text.forEach { add(JLabel(it).style()) }
        border = BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.BLACK, Color.GRAY)
        background = if (selected) colorSelected else colorNormal

        addMouseListener(object : MouseAdapter() {

            private var menu: JPopupMenu? = null

            override fun mousePressed(e: MouseEvent) {
                if (e.isPopupTrigger()) {
                    menu = PopUp(item).apply { show(e.component, e.x, e.y) }
                } else {
                    mouseDown = true
                    setBackground()
                    listner.onItemClicked(item)
                }
            }

            override fun mouseReleased(e: MouseEvent) {
                if (e.isPopupTrigger()) {
                    menu?.hide()
                } else {
                    mouseDown = false
                    setBackground()
                }
            }
        })
    }

    private fun setBackground() {
        background = if (mouseDown) colorClick else if (selected) colorSelected else colorNormal
    }

    interface Listener {
        fun onItemClicked(sub: Subtitles.Subtitle)
        fun onPreviewClicked(sub: Subtitles.Subtitle)
    }

    inner class PopUp(private val sub: Subtitles.Subtitle) : JPopupMenu() {
        var anItem: JMenuItem

        init {
            anItem = JMenuItem("Preview").style()
            anItem.addActionListener { ae ->
                listner.onPreviewClicked(sub)
            }
            add(anItem)
        }
    }
}