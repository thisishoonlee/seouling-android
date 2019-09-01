package mobile.seouling.com.framework.network;

import android.content.Context;
import io.reactivex.*;
import io.reactivex.annotations.NonNull;
import mobile.seouling.com.framework.rx.RxNetwork;
import org.reactivestreams.Publisher;

/**
 * Created by jj on 8/14/17.
 */

public class ActiveNetworkTransformer<T> implements SingleTransformer<T, T>, ObservableTransformer<T, T>,
        FlowableTransformer<T, T>, MaybeTransformer<T, T>, CompletableTransformer {

    private final Context mContext;

    public ActiveNetworkTransformer(Context context) {
        mContext = context;
    }

    @Override
    public SingleSource<T> apply(@NonNull Single<T> upstream) {
        return isConnected().flatMapSingle(connected -> upstream);
    }

    @Override
    public Publisher<T> apply(@NonNull Flowable<T> upstream) {
        return isConnected().flatMapPublisher(connected -> upstream);
    }

    @Override
    public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
        return isConnected().flatMapObservable(connected -> upstream);
    }

    @Override
    public CompletableSource apply(@NonNull Completable upstream) {
        return isConnected().ignoreElement().andThen(upstream);
    }

    @Override
    public MaybeSource<T> apply(@NonNull Maybe<T> upstream) {
        return isConnected().flatMap(connected -> upstream);
    }


    private Maybe<Boolean> isConnected() {
        return RxNetwork.isConnected(mContext)
                .filter(connected -> connected)
                .firstElement();
    }
}
