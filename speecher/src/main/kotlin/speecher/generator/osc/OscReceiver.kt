package speecher.generator.osc

import com.illposed.osc.MessageSelector
import com.illposed.osc.OSCMessageEvent
import com.illposed.osc.transport.udp.OSCPortIn
import com.illposed.osc.transport.udp.OSCPortInBuilder
import speecher.util.wrapper.LogWrapper

class OscReceiver constructor(
    private val log: LogWrapper
) : OscContract.Receiver {
    private var portIn: OSCPortIn? = null

    init {
        log.tag(this)
    }

    private val allMessageSelector = object : MessageSelector {
        override fun isInfoRequired(): Boolean = false

        override fun matches(messageEvent: OSCMessageEvent?): Boolean = true
    }

    override fun start(port: Int) {
        portIn = OSCPortInBuilder()
            .setPort(port)
            .addPacketListener(OSCPortIn.defaultPacketListener())
            .addMessageListener(allMessageSelector) { e: OSCMessageEvent -> processEvent(e) }
            .build()
        //portIn = OSCPortIn(OSCPort.defaultSCOSCPort());
        portIn?.apply {
            //isDaemonListener = false
            startListening()
            if (isListening) {
                log.d("Osc reciever listening on address = [local: ${localAddress} remote: ${remoteAddress}].")
//            } else if (!isConnected) {
//                log.e("Osc reeciver NOT connected on address = [local: ${localAddress} remote: ${remoteAddress}].")
            } else if (!isListening) {
                log.e("Osc reciever NOT listening on address = [local: ${localAddress} remote: ${remoteAddress}].")
            } else {
                log.e("Osc reciever NOT working")
            }
        } ?: log.e("Osc reciever NOT created")
    }

    override fun shutdown() {
        portIn?.apply { if (isListening) stopListening() }
        portIn = null
    }

    private fun processEvent(e: OSCMessageEvent) {
        e.apply { log.d("osc event : ${message.address} -> ${message.arguments} info.typetags = [${message.info.argumentTypeTags.map { it }}]") }
    }
}