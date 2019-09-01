package mobile.seouling.com.framework.network;

import android.content.Context;
import io.reactivex.*;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Created by jj on 8/14/17.
 */

public class RxPauseAdapterFactory extends CallAdapter.Factory {

    private final Context mContext;

    public RxPauseAdapterFactory(Context context) {
        mContext = context;
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        Class<?> rawType = getRawType(returnType);
        if (rawType == Single.class) {
            CallAdapter<Object, Single> delegate =
                    (CallAdapter<Object, Single>) retrofit.nextCallAdapter(this, returnType, annotations);
            return new RxSinglePauseAdapter(mContext, delegate);
        } else if (rawType == Completable.class) {
            CallAdapter<Object, Completable> delegate =
                    (CallAdapter<Object, Completable>) retrofit.nextCallAdapter(this, returnType, annotations);
            return new RxCompletablePauseAdapter(mContext, delegate);
        } else if (rawType == Maybe.class) {
            CallAdapter<Object, Maybe> delegate =
                    (CallAdapter<Object, Maybe>) retrofit.nextCallAdapter(this, returnType, annotations);
            return new RxMaybePauseAdapter(mContext, delegate);
        } else if (rawType == Flowable.class) {
            CallAdapter<Object, Flowable> delegate =
                    (CallAdapter<Object, Flowable>) retrofit.nextCallAdapter(this, returnType, annotations);
            return new RxFlowablePauseAdapter(mContext, delegate);
        } else if (rawType == Observable.class) {
            CallAdapter<Object, Observable> delegate =
                    (CallAdapter<Object, Observable>) retrofit.nextCallAdapter(this, returnType, annotations);
            return new RxObservablePauseAdapter(mContext, delegate);
        }
        return null;
    }

    private static abstract class RxPauseAdapter<U> implements CallAdapter<Object, Object> {

        protected final CallAdapter<Object, U> mCallAdapter;
        protected final Context mContext;

        RxPauseAdapter(Context context, CallAdapter<Object, U> callAdapter) {
            mContext = context;
            mCallAdapter = callAdapter;
        }

        @Override
        public final Type responseType() {
            return mCallAdapter.responseType();
        }

        @Override
        public abstract Object adapt(Call<Object> call);
    }

    private static class RxSinglePauseAdapter extends RxPauseAdapter<Single> {

        RxSinglePauseAdapter(Context context, CallAdapter<Object, Single> callAdapter) {
            super(context, callAdapter);
        }

        @Override
        public Object adapt(Call<Object> call) {
            Single<?> o = mCallAdapter.adapt(call);
            return o.compose(new ActiveNetworkTransformer<>(mContext));
        }
    }

    private static class RxCompletablePauseAdapter extends RxPauseAdapter<Completable> {

        RxCompletablePauseAdapter(Context context, CallAdapter<Object, Completable> callAdapter) {
            super(context, callAdapter);
        }

        @Override
        public Object adapt(Call<Object> call) {
            Completable o = mCallAdapter.adapt(call);
            return o.compose(new ActiveNetworkTransformer<>(mContext));
        }
    }

    private static class RxMaybePauseAdapter extends RxPauseAdapter<Maybe> {

        RxMaybePauseAdapter(Context context, CallAdapter<Object, Maybe> callAdapter) {
            super(context, callAdapter);
        }

        @Override
        public Object adapt(Call<Object> call) {
            Maybe<?> o = mCallAdapter.adapt(call);
            return o.compose(new ActiveNetworkTransformer<>(mContext));
        }
    }

    private static class RxFlowablePauseAdapter extends RxPauseAdapter<Flowable> {

        RxFlowablePauseAdapter(Context context, CallAdapter<Object, Flowable> callAdapter) {
            super(context, callAdapter);
        }

        @Override
        public Object adapt(Call<Object> call) {
            Flowable<?> o = mCallAdapter.adapt(call);
            return o.compose(new ActiveNetworkTransformer<>(mContext));
        }
    }

    private static class RxObservablePauseAdapter extends RxPauseAdapter<Observable> {

        RxObservablePauseAdapter(Context context, CallAdapter<Object, Observable> callAdapter) {
            super(context, callAdapter);
        }

        @Override
        public Object adapt(Call<Object> call) {
            Observable<?> o = mCallAdapter.adapt(call);
            return o.compose(new ActiveNetworkTransformer<>(mContext));
        }
    }
}
