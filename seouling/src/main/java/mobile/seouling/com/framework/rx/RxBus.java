package mobile.seouling.com.framework.rx;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import mobile.seouling.com.BuildEnv;
import mobile.seouling.com.framework.Log;

/**
 * Created by jj on 8/4/17.
 */

@SuppressWarnings("WeakerAccess")
public class RxBus {
    private static final String TAG = "RxBus";
    private static final boolean DEBUG = BuildEnv.DEBUG;

    private final Subject<Object> mBus;

    public RxBus() {
        mBus = PublishSubject.create().toSerialized();
        if (DEBUG) {
            mBus.doOnEach(noti -> {
                if (noti.isOnNext()) {
                    Log.d(TAG, "=> " + noti.getValue());
                } else if (noti.isOnError()) {
                    Log.d(TAG, "X : " + noti.getError());
                } else if (noti.isOnComplete()) {
                    Log.d(TAG, "|");
                }
            }).subscribe();
        }
    }

    public void post(Object o) {
        mBus.onNext(o);
    }

    public Observable<Object> toObservable() {
        return mBus;
    }

    public static RxBus getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        static final RxBus INSTANCE = new RxBus();
    }
}
