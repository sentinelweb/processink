package cubes.osc

class OscTypeTagsParser {
    fun parse(typeTags: CharSequence): List<String> {
        val typesSplit = mutableListOf<String>()
        var inArray = false
        var inArrayBuf: String? = null
        typeTags.forEach {
            if (!inArray) {
                if ('[' == it) {
                    inArrayBuf = it.toString()
                    inArray = true
                } else {
                    typesSplit.add(it.toString())
                }
            } else {
                if (']' == it) {
                    inArrayBuf += it
                    inArray = false
                    typesSplit.add(inArrayBuf!!)
                } else {
                    inArrayBuf += it
                }
            }
        }

        return typesSplit
    }
}