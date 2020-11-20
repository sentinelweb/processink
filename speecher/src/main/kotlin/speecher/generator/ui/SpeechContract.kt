package speecher.generator.ui

interface SpeechContract {
    interface View {

        fun showWindow()
    }

    interface Presenter {

        fun showWindow()
        fun moveCursorForward()
        fun moveCursorBack()
        fun play()
        fun pause()
    }
}