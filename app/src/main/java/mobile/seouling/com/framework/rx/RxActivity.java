package mobile.seouling.com.framework.rx;

import android.os.Bundle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import io.reactivex.Completable;
import mobile.seouling.com.BuildEnv;
import mobile.seouling.com.application.common.ISessionActivity;
import mobile.seouling.com.framework.Log;
import mobile.seouling.com.framework.session.SessionManager;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class RxActivity extends RxAppCompatActivity {

    private final String TAG = ((Object) this).getClass().getSimpleName();

    private boolean mIsStarted;
    private boolean mIsForeground;

    public Completable getLifecycleSignal(ActivityEvent filter) {
        return lifecycle().filter(event -> event == filter).firstElement().ignoreElement().onErrorComplete();
    }

    /**
     * To enable Lifecycle logging:
     * adb shell setprop log.tag.VingleActivity DEBUG
     */
    private static final String LOGTAG_LIFECYCLE = "MallangActivity";
    private static final boolean LOG_LIFECYCLE = BuildEnv.DEBUG && Log.isLoggable(LOGTAG_LIFECYCLE);

    public RxActivity() {
        super();
        if (LOG_LIFECYCLE) {
            lifecycle().subscribe(event -> {
                Log.d(LOGTAG_LIFECYCLE, TAG + " -> " + event);
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // First activity creation means that it will be in foreground shortly.
        // Without this, all the occurrence of ShowFragmentEvent in onCreate() will be ignored.
        mIsForeground = true;
    }

    public boolean isStarted() {
        return mIsStarted;
    }

    public boolean isForegroundState() {
        return mIsForeground;
    }

    @Override
    protected void onStart() {
        mIsStarted = true;
        try {
            super.onStart();
        } catch (NullPointerException e) {
//            https://fabric.io/vingle/android/apps/com.vingle.android/issues/58c32c820aeb16625b3e9c86
            Log.e(TAG, "NPE while onRestart", e);
        }
        if (this instanceof ISessionActivity) {
            SessionManager.getInstance().onActivityStart(this);
        }
    }

    @Override
    protected void onStop() {
        mIsStarted = false;
        if (this instanceof ISessionActivity) {
            SessionManager.getInstance().onActivityStop(this);
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        mIsForeground = true;
        try {
            super.onResume();
        } catch (RuntimeException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            getSupportFragmentManager().dump("", null, pw, null);
            Log.wtf(TAG, "onResume e:" + e + ", dump:" + sw.toString());
        }
    }

    @Override
    protected void onPause() {
        mIsForeground = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (this instanceof ISessionActivity) {
            SessionManager.getInstance().onActivityDestroy(this);
        }
        try {
            super.onDestroy();
        } catch (IllegalStateException e) {
            Log.e(TAG, "error at onDestroy: " + this + ", e:" + e);
        }
    }
}

