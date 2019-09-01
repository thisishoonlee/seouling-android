package mobile.seouling.com.framework.activity_helper;

import androidx.annotation.NonNull;
import mobile.seouling.com.application.common.BaseActivity;
import mobile.seouling.com.framework.BaseEventBus;

import java.lang.ref.WeakReference;

public abstract class EventDispatcher {

    public static class NullActivityException extends Exception {
    }

    private WeakReference<BaseActivity> mFragActRef = null;

    public void onActivityCreate(BaseActivity activity) {
        mFragActRef = new WeakReference<>(activity);
        BaseEventBus.getInstance().register(this);
    }

    public void onActivityDestroy() {
        BaseEventBus.getInstance().unregister(this);
    }

    @NonNull
    public BaseActivity getActivity() throws NullActivityException {
        if (mFragActRef != null) {
            BaseActivity activity = mFragActRef.get();
            if (activity != null && !activity.isFinishing()) {
                return activity;
            }
        }
        throw new NullActivityException();
    }
}