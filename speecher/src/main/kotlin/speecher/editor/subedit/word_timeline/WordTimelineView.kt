package speecher.editor.subedit.word_timeline

import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ext.getOrCreateScope
import java.awt.Color
import java.awt.Graphics
import java.awt.Rectangle
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel

class WordTimelineView() : JPanel(), WordTimelineContract.View {

    private val scope = this.getOrCreateScope()
    private val state: WordTimelineState = scope.get()
    private val presenter: WordTimelineContract.Presenter = scope.get()
    override val external: WordTimelineContract.External = presenter as WordTimelineContract.External

    private val clickRegions: MutableList<Rectangle> = mutableListOf()

    init {
        addMouseListener(object : MouseAdapter() {

            override fun mousePressed(e: MouseEvent) {
                clickRegions.forEachIndexed { i, region ->
                    if (e.x > region.x && e.x < region.x + region.width) {
                        println("click: $i")
                        presenter.onIndexSelected(i)
                        return
                    }
                }
            }
        })
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        g.color = Color.RED
        g.drawRect(0, 0, width, height)
        val distSec = state.limits[1] - state.limits[0]
        if (distSec > 0) {
            clickRegions.clear()
            state.subs.forEach { sub ->
                val positionStart = (width * (sub.fromSec - state.limits[0]) / distSec).toInt()
                val positionEnd = (width * (sub.toSec - state.limits[0]) / distSec).toInt()
                g.color = Color.BLACK
                g.drawString(sub.text[0], positionStart + 2, height - 5)
                g.color = Color.BLUE
                g.drawRect(positionStart, 0, positionEnd - positionStart, height)
                clickRegions.add(Rectangle(positionStart, 0, positionEnd - positionStart, height))
            }
            state.currentWord?.let { currentWord ->
                val positionStart = (width * (state.currentWordLimits[0] - state.limits[0]) / distSec).toInt()
                val positionEnd = (width * (state.currentWordLimits[1] - state.limits[0]) / distSec).toInt()
                g.color = Color.decode("#008800")
                g.drawString(currentWord, positionStart + 2, height - 5)
                g.drawRect(positionStart, 0, positionEnd - positionStart, height)
            }
        }
    }

    override fun update() {
        repaint()
    }

    companion object {
        @JvmStatic
        val scope = module {
            scope(named<WordTimelineView>()) {
                scoped<WordTimelineContract.View> { getSource() }
                scoped<WordTimelineContract.Presenter> { WordTimelinePresenter(get(), get()) }
                scoped { WordTimelineState() }
            }
        }
    }
}