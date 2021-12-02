package cubes.osc

import cubes.CubesContract
import io.reactivex.Observable

interface OscContract {

    interface Receiver {
        fun start(port: Int)
        fun shutdown()
        fun setController(c: Controller)
        fun isRunning(): Boolean
    }

    interface Controller {
        fun processEvent(e: OscEvent)
    }

    interface External {
        var listener: Listener
        fun initialise()
        fun shutdown()
        fun isRunning(): Boolean
        fun events(): Observable<CubesContract.Event>
    }

    data class OscEvent(
        val message: String,
        val args: List<Arg>

    ) {
        data class Arg(
            val value: Any?,
            val type: String
        )
    }

    interface Listener {
        fun onReceiverStarted()
        fun onReceiverStopped()
    }


}