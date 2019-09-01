package mobile.seouling.com.framework.util

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.lang.ref.WeakReference

object KeyboardUtils {

    private const val TAG = "KeyboardUtils"

    private val handler by lazy { Handler(Looper.getMainLooper()) }

    @JvmStatic
    @JvmOverloads
    fun show(view: View?, flag: Int = InputMethodManager.SHOW_FORCED): Boolean {
        view ?: return false
        return showSoftInput(view, flag)
    }

    @JvmStatic
    fun hide(view: View?): Boolean {
        view ?: return false
        return hideSoftInput(view)
    }

    @JvmStatic
    @JvmOverloads
    fun show(activity: Activity?, flags: Int = InputMethodManager.SHOW_FORCED): Boolean {
        activity ?: return false
        return showSoftInput(activity.focusedView, flags)
    }

    @JvmStatic
    fun hide(activity: Activity?): Boolean {
        activity ?: return false
        return hideSoftInput(activity.focusedView)
    }

    private fun showSoftInput(view: View, flags: Int = InputMethodManager.SHOW_FORCED): Boolean {
        val imm = view.context.inputMethodManager ?: return false
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        view.requestFocus()
        val viewRef = view.weakRef()
        return imm.showSoftInput(view, flags, object : ResultReceiver(handler) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                val v = viewRef.get() ?: return
                if (resultCode == InputMethodManager.RESULT_UNCHANGED_HIDDEN || resultCode == InputMethodManager.RESULT_HIDDEN) {
                    toggleSoftInput(v.context)
                }
            }
        })
    }

    private fun hideSoftInput(view: View): Boolean {
        val imm = view.context.inputMethodManager ?: return false
        val viewRef = view.weakRef()
        return imm.hideSoftInputFromWindow(view.windowToken, 0, object : ResultReceiver(handler) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                val v = viewRef.get() ?: return
                if (resultCode == InputMethodManager.RESULT_UNCHANGED_SHOWN || resultCode == InputMethodManager.RESULT_SHOWN) {
                    toggleSoftInput(v.context)
                }
            }
        })
    }

    @JvmStatic
    private fun toggleSoftInput(context: Context) {
        context.inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    @JvmStatic
    fun fixSoftInputLeaks(application: Application) {
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                fixSoftInputLeaks(activity)
            }
        })
    }

    fun fixSoftInputLeaks(activity: Activity) {
        val imm = activity.inputMethodManager ?: return
        val leakViews = arrayOf("mLastSrvView", "mCurRootView", "mServedView", "mNextServedView")
        for (leakView in leakViews) {
            try {
                val leakViewField = InputMethodManager::class.java.getDeclaredField(leakView) ?: continue
                if (!leakViewField.isAccessible) {
                    leakViewField.isAccessible = true
                }
                val obj = leakViewField.get(imm) as? View ?: continue
                if (obj.rootView === activity.window.decorView.rootView) {
                    leakViewField.set(imm, null)
                }
            } catch (ignore: Throwable) {
                Log.w(TAG, "fixSoftInputLeaks($leakView)", ignore)
            }
        }
    }

    private val Context.inputMethodManager: InputMethodManager?
        get() = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

    private val Activity.focusedView: View
        get() = currentFocus ?: findViewById(android.R.id.content) ?: View(this)

    private fun <T> T.weakRef(): WeakReference<T> {
        return WeakReference(this)
    }
}
