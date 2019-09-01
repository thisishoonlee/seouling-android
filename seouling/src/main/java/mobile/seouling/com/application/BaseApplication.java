package mobile.seouling.com.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import io.fabric.sdk.android.Fabric;
import mobile.seouling.com.application.network.BaseRequestUrl;
import mobile.seouling.com.application.network.OkHttpProviderImpl;
import mobile.seouling.com.framework.CrashHelper;
import mobile.seouling.com.framework.network.BaseRetrofit;
import mobile.seouling.com.framework.network.IOkHttpProvider;

public class BaseApplication extends Application {

    private static Context sAppContext;
    private static IOkHttpProvider sOkHttpProvider;

    @Override
    public void onCreate() {
        sAppContext = getApplicationContext();

        sOkHttpProvider = new OkHttpProviderImpl(getApplicationContext());

        initFabric(getContext());
        initRetrofit(sAppContext);
        initGlide(sAppContext);

        super.onCreate();
    }

    public static Context getContext() {
        return sAppContext;
    }

    private void initFabric(Context context) {
        Fabric.with(context, CrashHelper.start(), new Answers(), new Crashlytics());
    }

    private static void initGlide(Context appContext) {
        ActivityManager am = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        final Glide glide = Glide.get(appContext);
        if (am != null && am.getLargeMemoryClass() <= (int) (Runtime.getRuntime().maxMemory() / (1024 * 1024))) {
            glide.setMemoryCategory(MemoryCategory.HIGH);
        }
    }

    public static void initRetrofit(Context appContext) {
        BaseRetrofit.init(appContext, BaseRequestUrl.get(), sOkHttpProvider);
    }

}
