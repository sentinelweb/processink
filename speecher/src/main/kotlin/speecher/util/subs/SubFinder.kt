package speecher.util.subs

import speecher.domain.Subtitles

class SubFinder {

    /**
     * Finds matching text if times overlap (made for single word matching - so just matches 0th string in list)
     */
    fun findOverlapping(sub: Subtitles.Subtitle, list: List<Subtitles.Subtitle>): Int? =
        list
            .find { it.text[0] == sub.text[0] && it.fromSec <= sub.toSec && it.toSec >= sub.fromSec }
            ?.let { list.indexOf(it) }

    /**
     * finds elements that have some protion inside the range
     */
    fun getRangeInclusive(sub: Subtitles.Subtitle, subs: Subtitles): List<Subtitles.Subtitle> =
        subs.timedTexts.filter {
            (it.fromSec >= sub.fromSec && it.toSec <= sub.toSec)
                    || (it.toSec >= sub.fromSec && it.toSec <= sub.toSec)
                    || (it.fromSec <= sub.toSec && it.fromSec >= sub.fromSec)
        }

    /**
     * finds elements that have some protion inside the range
     */
    fun getRangeInclusive(fromSec: Float, toSec: Float, subs: Subtitles): List<Subtitles.Subtitle> =
        subs.timedTexts.filter {
            (it.fromSec > fromSec && it.toSec < toSec)
                    || (it.toSec >= fromSec && it.toSec <= toSec)
                    || (it.fromSec <= toSec && it.fromSec >= fromSec)
        }

    /**
     * finds elements that entirely inside the range
     */
    fun getRangeExclusive(sub: Subtitles.Subtitle, subs: Subtitles): List<Subtitles.Subtitle> =
        subs.timedTexts.filter {
            it.fromSec >= sub.fromSec && it.toSec <= sub.toSec
        }

    /**
     * finds elements that entirely inside the range
     */
    fun getRangeExclusive(fromSec: Float, toSec: Float, subs: Subtitles): List<Subtitles.Subtitle> =
        subs.timedTexts.filter {
            it.fromSec >= fromSec && it.toSec <= toSec
        }

    /**
     * Checks if all element in write list are in read list (in order and makes a map)
     */
    fun buildMapSimple(readWordList: List<String>, subs: List<Subtitles.Subtitle>): Map<Int, Int> {
        val writeWords = subs.map { it.text[0] }
        var found = -1
        run loop@{
            (0..readWordList.size - 1).forEach read@{ position ->
                (0..writeWords.size - 1).forEach { wposition ->
                    //print("$position $wposition -- ${readWordList[position + wposition]} -> ${writeWords[wposition]} ")
                    if (position + wposition < readWordList.size
                        && readWordList[position + wposition] != writeWords[wposition]
                    ) {
                        //println("no match")
                        return@read
                    }
                    //println("match")
                }
                found = position
                return@loop
            }
        }
        val mutableMapOf = mutableMapOf<Int, Int>()
        if (found > -1) {
            (0..writeWords.size - 1).forEach {
                mutableMapOf.put(it + found, it)
            }
        }
        return mutableMapOf
    }

    fun buildMapCorrelate(readWordList: List<String>, subs: List<Subtitles.Subtitle>): Map<Int, Int> {
        // find best position
        val writeWordList = subs.map { it.text[0] }
        val scores = correlate(readWordList, writeWordList)
        // build map
        val max = scores.maxOrNull()
        return mutableMapOf()
    }

    fun correlate(readWordList: List<String>, writeWordList: List<String>): List<Int> {
        val scores = mutableListOf<Int>()//readWordList.size + writeWordList.size - 1
        (/*-(writeWordList.size - 1)*/0..(readWordList.size - 1)).forEach { position ->
            val (rstart, wstart) = if (position < 0) {
                Pair(0, -position)
            } else {
                Pair(position, 0)
            }
            var score = 0
            var i = 0
            var rpos = rstart + i
            var wpos = wstart + i
            while (rpos < readWordList.size && wpos < writeWordList.size) {
                score += if (readWordList[rpos] == writeWordList[wpos]) 1 else 0
                i++
                rpos = rstart + i
                wpos = wstart + i
            }
            scores.add(score)
        }
        return scores
    }


}