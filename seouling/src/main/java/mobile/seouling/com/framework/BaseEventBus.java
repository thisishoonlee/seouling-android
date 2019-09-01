package mobile.seouling.com.framework;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class BaseEventBus extends Bus {

    public static final String TAG = "MallangEventBus";

    private static BaseEventBus mMallangBus;
    private final Handler mUIHandler;

    public static BaseEventBus getInstance() {
        if (mMallangBus == null) {
            mMallangBus = new BaseEventBus(ThreadEnforcer.ANY);
        }
        return mMallangBus;
    }

    @Override
    public void register(@NonNull Object object) {
        if (Log.isLoggable(TAG)) {
            Log.v(TAG, "register " + object);
        }
        try {
            super.register(object);
        } catch (IllegalArgumentException | NoClassDefFoundError e) {
            Log.wtf(TAG, "unexpected error while unregister eventbus", e);
        }
    }

    @Override
    public void unregister(@NonNull Object object) {
        if (Log.isLoggable(TAG)) {
            Log.v(TAG, "unregister " + object);
        }
        try {
            super.unregister(object);
        } catch (IllegalArgumentException | NoClassDefFoundError e) {
            Log.e(TAG, "unexpected error while unregister eventbus", e);
        }
    }

    private BaseEventBus(ThreadEnforcer enforcer) {
        super(enforcer);
        mUIHandler = new Handler(Looper.getMainLooper());
    }

    public void postAsync(Object event) {
        // post it inside main thread.
        mUIHandler.post(new EventRunnable(event));
    }

    public void postDelayed(Object event, long time) {
        mUIHandler.postDelayed(new EventRunnable(event), time);
    }

    private class EventRunnable implements Runnable {
        final private Object mEvent;

        private EventRunnable(Object event) {
            mEvent = event;
        }

        @Override
        public void run() {
            post(mEvent);
        }
    }

    @Override
    public void post(Object event) {
        try {
            if (Log.isLoggable(TAG)) {
                Log.d(TAG, "post " + event);
            }
            super.post(event);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}
