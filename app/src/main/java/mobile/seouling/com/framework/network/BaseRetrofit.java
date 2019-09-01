package mobile.seouling.com.framework.network;

import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.schedulers.Schedulers;
import mobile.seouling.com.framework.GsonFactory;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseRetrofit {

    private static final String TAG = "MallangRetrofit";
    private static Retrofit.Builder sRetrofitBuilder;
    private static Retrofit sRetrofit;
    private static String sBaseUrl;

    public static void init(Context context, String baseUrl, IOkHttpProvider okHttpProvider) {
        sRetrofitBuilder = new Retrofit.Builder()
                .client(okHttpProvider.getMallangApi())
                .addCallAdapterFactory(new RxPauseAdapterFactory(context))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create(GsonFactory.getGson()));
        sRetrofit = sRetrofitBuilder.baseUrl(baseUrl).build();
        sBaseUrl = baseUrl;
    }

    public static <T> T create(@NonNull Class<T> service, @Nullable String testUrl) {
        if (testUrl != null) {
            return sRetrofitBuilder.baseUrl(testUrl).build().create(service);
        }
        String oldBaseUrl = sRetrofit.baseUrl().toString();
        String newBaseUrl = getBaseUrl();
        if (!TextUtils.equals(oldBaseUrl, newBaseUrl)) {
            synchronized (BaseRetrofit.class) {
                sRetrofit = sRetrofitBuilder.baseUrl(newBaseUrl).build();
            }
        }
        return sRetrofit.create(service);
    }

    public static <T> T create(@NonNull Class<T> service) {
        return create(service, null);
    }

    @NonNull
    private static String getBaseUrl() {
        return sBaseUrl;
    }
}
