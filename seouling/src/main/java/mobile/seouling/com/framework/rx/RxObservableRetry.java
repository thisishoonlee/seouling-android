package mobile.seouling.com.framework.rx;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import mobile.seouling.com.framework.Log;

public class RxObservableRetry implements Function<Observable<? extends Throwable>, Observable<?>> {

    private final int mMaxRetries;
    private final int mRetryDelay;
    private final TimeUnit mTimeUnit;
    private int mRetryCount;

    public RxObservableRetry(int maxRetries, int retryDelay, TimeUnit timeUnit) {
        mMaxRetries = maxRetries;
        mRetryDelay = retryDelay;
        mTimeUnit = timeUnit;
        mRetryCount = 0;
    }

    public int getRetryCount() {
        return mRetryCount;
    }

    @Override
    public Observable<?> apply(Observable<? extends Throwable> observable) {
        return observable.flatMap((t) -> {
            if (mRetryCount < mMaxRetries) {
                ++mRetryCount;
                onRetry(mRetryCount, t);
                // When this Observable calls onNext, the original
                // Observable will be retried (i.e. re-subscribed).
                return Observable.timer(mRetryDelay, mTimeUnit, Schedulers.computation());
            }
            // Max retries hit. Just pass the error along.
            return Observable.error(t);
        });
    }

    protected void onRetry(int retryCount, Throwable t) {
    }

    public static RxObservableRetry getDefault() {
        return new RxObservableRetry(2, 1, TimeUnit.SECONDS) {
            @Override
            protected void onRetry(int retryCount, Throwable t) {
                Log.d("RxRetry", "retry " + retryCount + " / " + t.getMessage());
            }
        };
    }
}