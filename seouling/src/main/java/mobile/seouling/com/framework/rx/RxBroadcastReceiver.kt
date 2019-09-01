package mobile.seouling.com.framework.rx

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Looper

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

object RxBroadcastReceiver {
    fun create(context: Context, intentFilter: IntentFilter): Observable<Intent> {
        return Observable.create { emitter ->
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(c: Context, intent: Intent?) {
                    intent?.let { emitter.onNext(it) }
                }
            }
            context.registerReceiver(receiver, intentFilter)
            emitter.setCancellable {
                if (Looper.getMainLooper() == Looper.myLooper()) {
                    context.unregisterReceiver(receiver)
                } else {
                    val inner = AndroidSchedulers.mainThread().createWorker()
                    inner.schedule {
                        context.unregisterReceiver(receiver)
                        inner.dispose()
                    }
                }
            }
        }
    }
}
