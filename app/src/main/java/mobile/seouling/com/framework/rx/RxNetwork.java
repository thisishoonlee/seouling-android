package mobile.seouling.com.framework.rx;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.core.net.ConnectivityManagerCompat;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposables;

public class RxNetwork {
    public static Observable<Boolean> isConnected(Context context) {
        Context appContext = context.getApplicationContext();
        return Observable.create(new ConnectivityActionSubscribe(appContext))
                .flatMap(networkInfo -> Observable.just(isActiveNetworkConnected(appContext)))
                .startWith(isActiveNetworkConnected(appContext))
                .distinctUntilChanged();
    }

    public static Observable<Boolean> isMetered(Context context) {
        Context appContext = context.getApplicationContext();
        return Observable.create(new ConnectivityActionSubscribe(appContext))
                .flatMap(networkInfo -> Observable.just(isActiveNetworkMetered(appContext)))
                .startWith(isActiveNetworkMetered(appContext))
                .distinctUntilChanged();
    }

    private static boolean isActiveNetworkConnected(Context context) {
        NetworkInfo networkInfo = getConnectivityManager(context).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private static boolean isActiveNetworkMetered(Context context) {
        return ConnectivityManagerCompat.isActiveNetworkMetered(getConnectivityManager(context));
    }

    private static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private static class ConnectivityActionSubscribe implements ObservableOnSubscribe<Intent> {

        private final Context mContext;

        ConnectivityActionSubscribe(Context context) {
            mContext = context;
        }

        @Override
        public void subscribe(@NonNull ObservableEmitter<Intent> emitter) throws Exception {
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                        emitter.onNext(intent);
                    }
                }
            };
            emitter.setDisposable(Disposables.fromRunnable(() -> mContext.unregisterReceiver(receiver)));
            mContext.registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION), null, null);
        }
    }
}