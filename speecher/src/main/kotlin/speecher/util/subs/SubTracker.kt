package speecher.util.subs

class SubTracker {
//
//    private fun checkReadSubtitle(pos: Float) {
//        @Suppress("ControlFlowWithEmptyBody")
//        if (state.currentReadIndex > -1
//            && (state.srtRead?.timedTexts?.get(state.currentReadIndex)?.between(pos) ?: false)
//        ) {
//
//        } else if (state.currentReadIndex > -1) {
//            state.lastReadIndex = state.currentReadIndex
//            state.currentReadIndex = -1
//        } else {
//            scanForReadSubtitle(pos)
//        }
//    }
//
//    private fun scanForReadSubtitle(positionSec: Float) {
//        state.srtRead?.timedTexts?.let { texts ->
//            val startIndex = state.lastReadIndex
//            (startIndex + 1..texts.size - 1).forEach { testIndex ->
//                val get = texts.get(testIndex)
//                if (get.between(positionSec)) {
//                    state.currentReadIndex = testIndex
//                    return
//                }
//                if (get.toSec < positionSec) {
//                    state.lastReadIndex = testIndex
//                    return
//                }
//            }
//        }
//        state.lastReadIndex = -1
//    }
}