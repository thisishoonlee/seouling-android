package mobile.seouling.com.framework.session

import android.app.Activity
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import mobile.seouling.com.BuildEnv
import mobile.seouling.com.application.main.MainActivity
import mobile.seouling.com.framework.Log
import mobile.seouling.com.framework.ThreadHelper
import mobile.seouling.com.framework.helper.FormatHelper
import java.util.*
import java.util.concurrent.TimeUnit


class SessionManager private constructor() {
    private val activities = WeakHashMap<Activity, Boolean>()
    private var sessionIdleSub: Disposable? = null
    private var sessionTimeoutSub: Disposable? = null

    val sessionId: String
        get() = session.id

    val session: Session by lazy {
        Session(
            id = FormatHelper.toISO8601(System.currentTimeMillis()),
            state = SessionState.Start,
            intent = null
        ).also {
            if (BuildEnv.DEBUG || Log.isLoggable(TAG)) {
                Log.d(TAG, "session=$it")
            }
        }
    }

    private fun resetTimer() {
        sessionIdleSub?.dispose()
        sessionTimeoutSub?.dispose()
    }

    fun onActivityStart(activity: Activity) {
        activities[activity] = true
        resetTimer()
        if (activity is MainActivity) {
            session.intent = activity.getIntent()
        }
        updateSessionState(SessionState.Start)
    }

    private fun updateSessionState(state: SessionState) {
        if (session.state == state) return
        session.state = state
        logSession()
    }

    private fun existsForegroundActivity(): Boolean {
        return activities.containsValue(true)
    }

    // Start, Idle, Stop
    fun onActivityStop(activity: Activity) {
        activities[activity] = false
        resetTimer()
        sessionIdleSub = timer(2).subscribe({
            if (!existsForegroundActivity()) {
                updateSessionState(SessionState.Idle)
            }
            if (activities.isEmpty()) {
                updateSessionState(SessionState.Stop)
            } else if (!existsForegroundActivity()) {
                sessionTimeoutSub = timer(SESSION_TIMEOUT_SECONDS).subscribe({
                    if (!existsForegroundActivity()) {
                        updateSessionState(SessionState.Stop)
                    }
                }, { error -> Log.e(TAG, "stop timer error $error") })
            }
        }, { error -> Log.e(TAG, "idle timer error $error") })
    }

    private fun logSession() {
        if (BuildEnv.DEBUG || Log.isLoggable(TAG)) {
            Log.d(TAG, "session=$session")
        }
    }

    private fun timer(time: Long) = Completable
        .timer(time, TimeUnit.SECONDS, Schedulers.computation())
        .observeOn(ThreadHelper.mainScheduler())

    fun onActivityDestroy(activity: Activity) {
        activities.remove(activity)
    }

    companion object {

        private const val TAG = "SessionManager"

        const val SESSION_TIMEOUT_SECONDS: Long = 15 * 60 // 15 minutes

        @JvmStatic
        val instance: SessionManager by lazy { SessionManager() }
    }
}