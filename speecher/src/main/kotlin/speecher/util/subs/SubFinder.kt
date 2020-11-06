package speecher.util.subs

import speecher.domain.Subtitles

class SubFinder {

    /**
     * Finds matching text if times overlap (made for single word matching - so just matches 0th string in list)
     */
    fun findOverlapping(sub: Subtitles.Subtitle, list: List<Subtitles.Subtitle>): Int? =
        list
            .find { it.text[0] == sub.text[0] && it.fromSec < sub.toSec && it.toSec > sub.fromSec }
            ?.let { list.indexOf(it) }

}