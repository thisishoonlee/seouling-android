package mobile.seouling.com.framework.rx;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.reactivestreams.Publisher;

import androidx.annotation.NonNull;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;

public class ResumeTransformer<T> implements SingleTransformer<T, T>, MaybeTransformer<T, T>,
        ObservableTransformer<T, T>, FlowableTransformer<T, T> {

    private final Observable<?> mResumeEvent;

    public ResumeTransformer(LifecycleProvider<?> provider) {
        mResumeEvent = provider.lifecycle().filter(e -> e == FragmentEvent.RESUME || e == ActivityEvent.RESUME);
    }

    @Override
    public SingleSource<T> apply(@NonNull Single<T> upstream) {
        return upstream.flatMap(o -> mResumeEvent.firstOrError().map(__ -> o));
    }

    @Override
    public Publisher<T> apply(@NonNull Flowable<T> upstream) {
        return upstream.flatMapMaybe(o -> mResumeEvent.firstElement().map(__ -> o));
    }

    @Override
    public MaybeSource<T> apply(@NonNull Maybe<T> upstream) {
        return upstream.flatMap(o -> mResumeEvent.firstElement().map(__ -> o));
    }

    @Override
    public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
        return upstream.flatMapMaybe(o -> mResumeEvent.firstElement().map(__ -> o));
    }

    public static <T> ResumeTransformer<T> create(LifecycleProvider<?> provider) {
        return new ResumeTransformer<>(provider);
    }
}