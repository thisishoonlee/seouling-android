package mobile.seouling.com.application.common.fragment;

import android.os.Handler;
import com.trello.rxlifecycle2.android.FragmentEvent;
import io.reactivex.Completable;
import mobile.seouling.com.BuildEnv;
import mobile.seouling.com.framework.Log;
import mobile.seouling.com.framework.ThreadHelper;
import mobile.seouling.com.framework.rx.MainThreadScheduler;

public abstract class RxFragment extends com.trello.rxlifecycle2.components.support.RxFragment {
    protected static final Handler sHandler = ThreadHelper.mainHandler();

    private final String TAG = ((Object) this).getClass().getSimpleName();

    public Completable getLifecycleSignal(FragmentEvent lifecycle) {
        return lifecycle().filter(l -> l == lifecycle).firstElement().ignoreElement().onErrorComplete();
    }

    /**
     * To enable Lifecycle logging:
     * adb shell setprop log.tag.VingleFragemnt DEBUG
     */
    private static final String LOGTAG_LIFECYCLE = "MallangFragment";
    private static final boolean LOG_LIFECYCLE = BuildEnv.DEBUG && Log.isLoggable(LOGTAG_LIFECYCLE);

    public RxFragment() {
        if (LOG_LIFECYCLE) {
            lifecycle().subscribe(event -> {
                Log.d(LOGTAG_LIFECYCLE, TAG + " -> " + event);
            });
        }
    }

    public FragmentEvent getLifecycleEvent() {
        return lifecycle().firstElement().blockingGet();
    }

    @Override
    public void onDestroyView() {
        mViewHasDestroyed = true;
        super.onDestroyView();
    }

    public boolean isNotFirstViewCreation() {
        return mViewHasDestroyed;
    }

    private boolean mViewHasDestroyed = false;

    public static MainThreadScheduler getMainThreadScheduler() {
        return ThreadHelper.mainScheduler();
    }
}
