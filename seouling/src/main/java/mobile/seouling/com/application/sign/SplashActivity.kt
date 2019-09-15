package mobile.seouling.com.application.sign

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import com.trello.rxlifecycle2.android.ActivityEvent
import io.reactivex.Observable
import mobile.seouling.com.application.data_provider.BasePreference
import mobile.seouling.com.application.main.MainActivity
import mobile.seouling.com.framework.Log
import mobile.seouling.com.framework.compat.AppCompatUtils
import mobile.seouling.com.framework.rx.RxActivity
import mobile.seouling.com.framework.rx.SchedulerProvider
import mobile.seouling.com.framework.rx.kt.ErrorConsumer
import java.util.concurrent.TimeUnit

class SplashActivity : RxActivity() {

    private val schedulers by lazy { SchedulerProvider.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("Intent Test", "Splash onCreate")

        BasePreference.setIsSignedIn(true)

        delay(3000){
            Intent(this, SignInProcessActivity::class.java)
                    .run {
                        AppCompatUtils.startActivityWithShortFade(this@SplashActivity, this)
                        finish()
                    }
        }

//        if(intent.hasExtra(BaseConstant.BundleKey.EXTRA_INIT_FAIL)) {
//            showLogin()
//        } else {
//            showHome()
//        }
    }

    private fun showLogin() {
        val intent = Intent(this, SignInProcessActivity::class.java)
        Log.d("Intent Test", "Splash Show Login")
        try {
            startActivity(intent)
            finish()
        } catch (e: ActivityNotFoundException) {
            Log.wtf("SplashAc", "activity not found and retry", e)
            delay(500) {
                AppCompatUtils.startActivityWithLongFade(this, intent)
                finish()
            }
        }
    }

    private fun showHome() {
        val intent = Intent(this, MainActivity::class.java)
        mobile.seouling.com.framework.Log.d("Intent Test", "Splash Show Home")
        try {
            startActivity(intent)
            finish()
        } catch (e: ActivityNotFoundException) {
            Log.wtf("SplashAc", "activity not found and retry", e)
            delay(500) {
                AppCompatUtils.startActivityWithLongFade(this, intent)
                finish()
            }
        }
    }

    private fun delay(ms: Long, callback: () -> Unit) =
            Observable.timer(ms, TimeUnit.MILLISECONDS, schedulers.computation())
                    .observeOn(schedulers.ui())
                    .compose(bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe({ callback() }, ErrorConsumer())
}