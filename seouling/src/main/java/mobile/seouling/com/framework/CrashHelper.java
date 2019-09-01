package mobile.seouling.com.framework;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import androidx.annotation.NonNull;

import io.fabric.sdk.android.Kit;

public class CrashHelper {
    public static final String TAG = "CrashHelper";
    private static boolean sIsStarted = false;

    @NonNull
    public static Kit start() {
        sIsStarted = true;
        return new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder()
                        .build())
                .build();
    }

    static void log(Throwable t) {
        if (!sIsStarted) {
            return;
        }
        Crashlytics.logException(t);
    }

    static void log(String msg) {
        if (!sIsStarted) {
            return;
        }
        Crashlytics.log(msg);
    }

    static void log(int priority, String tag, String msg) {
        if (!sIsStarted) {
            return;
        }

        Crashlytics.log(priority, tag, msg);
    }

    static void log(int priority, String tag, String msg, Throwable tr) {
        if (!sIsStarted) {
            return;
        }

        Crashlytics.log(priority, tag, msg);
        if (priority >= android.util.Log.ERROR) {
            Crashlytics.logException(tr);
        }
    }

    public static void updateUserData(String username, String email, int id) {
        if (!sIsStarted) {
            return;
        }
        Crashlytics.setUserName(username);
        Crashlytics.setUserEmail(email);
        Crashlytics.setUserIdentifier(String.valueOf(id));
        //Crashlytics.setString("build_id", BuildConfig.GIT_REVISION);
    }
}

