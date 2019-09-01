package mobile.seouling.com.framework.rx.kt

import mobile.seouling.com.BuildEnv
import mobile.seouling.com.framework.Log
import java.util.concurrent.CancellationException

/**
 * Created by jj on 9/6/16.
 */
class ErrorConsumer(private val message: String? = "") : (Throwable) -> Unit {
    private val _message by lazy {
        if (message.isNullOrEmpty()) {
            if (BuildEnv.DEBUG) {
                Thread.currentThread().stackTrace[3].toString()
            } else {
                "error"
            }
        } else {
            message
        }
    }

    override fun invoke(t: Throwable) {
        if (t is CancellationException) {
            //ignore
        } else {
            Log.e(TAG, _message, t)
        }
    }

    companion object {
        private const val TAG = "ErrorConsumer"
    }
}
