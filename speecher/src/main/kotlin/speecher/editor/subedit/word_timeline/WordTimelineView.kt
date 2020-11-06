package speecher.editor.subedit.word_timeline

import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ext.getOrCreateScope
import java.awt.Color
import java.awt.Graphics
import javax.swing.JPanel

class WordTimelineView() : JPanel(), WordTimelineContract.View {
    private val scope = this.getOrCreateScope()
    private val state: WordTimelineState = scope.get()
    private val presenter: WordTimelineContract.Presenter = scope.get()
    override val external: WordTimelineContract.External = presenter as WordTimelineContract.External

    init {

    }

    override fun paint(g: Graphics) {
        super.paint(g)
        g.color = Color.RED
        g.drawRect(0, 0, width, height)
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