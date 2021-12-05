package cubes.osc

import com.illposed.osc.OSCMessageEvent

class OscMessageMapper(
    private val typeTagsParser: OscTypeTagsParser
) {
    fun map(e: OSCMessageEvent): OscContract.OscEvent {
        val args = typeTagsParser
            .parse(e.message.info.argumentTypeTags)
            .mapIndexed { i, type ->
                OscContract.OscEvent.Arg(e.message.arguments[i], type)
            }

        return OscContract.OscEvent(e.message.address, args)
    }
}