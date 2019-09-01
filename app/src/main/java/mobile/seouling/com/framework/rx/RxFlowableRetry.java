package mobile.seouling.com.framework.rx;

import mobile.seouling.com.framework.Log;
import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * http://stackoverflow.com/a/25292833
 */
public class RxFlowableRetry implements Function<Flowable<Throwable>, Publisher<?>> {

    private final int mMaxRetries;
    private final int mRetryDelay;
    private final TimeUnit mTimeUnit;
    private int mRetryCount;

    public RxFlowableRetry(int maxRetries, int retryDelay, TimeUnit timeUnit) {
        mMaxRetries = maxRetries;
        mRetryDelay = retryDelay;
        mTimeUnit = timeUnit;
        mRetryCount = 0;
    }

    public int getRetryCount() {
        return mRetryCount;
    }

    @Override
    public Publisher<?> apply(@NonNull Flowable<Throwable> flowable) throws Exception {
        return flowable.flatMap((t) -> {
            if (mRetryCount < mMaxRetries) {
                ++mRetryCount;
                onRetry(mRetryCount, t);
                // When this Single calls onNext, the original
                // Single will be retried (i.e. re-subscribed).
                return Flowable.timer(mRetryDelay, mTimeUnit, Schedulers.computation());
            }
            // Max retries hit. Just pass the error along.
            return Flowable.error(t);
        });
    }

    protected void onRetry(int retryCount, Throwable t) {
    }

    public static RxFlowableRetry getDefault() {
        return new RxFlowableRetry(2, 1, TimeUnit.SECONDS) {
            @Override
            protected void onRetry(int retryCount, Throwable t) {
                Log.d("RxRetry", "retry " + retryCount + " / " + t);
            }
        };
    }
}