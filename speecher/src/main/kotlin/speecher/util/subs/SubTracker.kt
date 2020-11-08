package speecher.util.subs

import speecher.domain.Subtitles

class SubTracker {

    interface SubProvider {
        fun getSubs(): Subtitles?
    }

    val currentSubtitle: String?
        get() = if (currentIndex > -1) {
            provider.getSubs()?.timedTexts?.get(currentIndex)?.text?.toString()
        } else null

    lateinit var provider: SubTracker.SubProvider

    private var currentIndex: Int = -1
    private var lastIndex: Int = -1

    fun checkSubtitle(pos: Float) {
        @Suppress("ControlFlowWithEmptyBody")
        if (currentIndex > -1
            && (provider.getSubs()?.timedTexts?.get(currentIndex)?.between(pos) ?: false)
        ) {

        } else if (currentIndex > -1) {
            lastIndex = currentIndex
            currentIndex = -1
        } else {
            scanForReadSubtitle(pos)
        }
    }

    fun scanForPosition(positionSec: Float) {
        currentIndex = -1
        lastIndex = -1
        scanForReadSubtitle(positionSec)
    }

    private fun scanForReadSubtitle(positionSec: Float) {
        provider.getSubs()?.timedTexts?.let { texts ->
            val startIndex = lastIndex
            (startIndex + 1..texts.size - 1).forEach { testIndex ->
                val get = texts.get(testIndex)
                if (get.between(positionSec)) {
                    currentIndex = testIndex
                    return
                }
                if (get.toSec < positionSec) {
                    lastIndex = testIndex
                    return
                }
            }
        }
        lastIndex = -1
    }
}