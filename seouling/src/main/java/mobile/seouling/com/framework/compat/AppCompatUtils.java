package mobile.seouling.com.framework.compat;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import mobile.seouling.com.R;

/**
 * Created by jisung on 2014. 11. 3..
 */
public class AppCompatUtils {

    /**
     * Checks the Android API level is supported
     *
     * @param apiLevel {@link Build.VERSION_CODES}
     */
    public static boolean isApiSupported(int apiLevel) {
        return Build.VERSION.SDK_INT >= apiLevel;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void setLocale(Configuration config, Locale locale) {
        if (isApiSupported(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
    }

    public static String getVersionName(Context context) {
        PackageInfo pInfo;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
        return pInfo.versionName;
    }

    public static void startActivityWithShortFade(Activity activity, Intent intent) {
        startActivityWithAnim(activity, intent, R.anim.stay, R.anim.splash_fade_out_short);
    }

    public static void startActivityWithLongFade(Activity activity, Intent intent) {
        startActivityWithAnim(activity, intent, R.anim.stay, R.anim.splash_fade_out_long);
    }

    public static void startActivityWithAnim(Activity activity, Intent intent, int inAnim, int outAnim) {
        try {
            if (isApiSupported(Build.VERSION_CODES.JELLY_BEAN)) {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(activity, inAnim, outAnim);
                ActivityCompat.startActivity(activity, intent, options.toBundle());
            } else {
                activity.startActivity(intent);
                activity.overridePendingTransition(inAnim, outAnim);
            }
        } catch (IllegalArgumentException ignored) {
            // https://fabric.io/vingle/android/apps/com.vingle.android/issues/54ae926965f8dfea158b6fd3
        }
    }

    private static int toMb(long bytes) {
        return (int) (bytes / (1024 * 1024));
    }

    public static boolean isLowMemDevice(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return toMb(Runtime.getRuntime().maxMemory()) < am.getLargeMemoryClass();
    }
}
