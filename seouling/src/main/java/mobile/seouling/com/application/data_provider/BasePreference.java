package mobile.seouling.com.application.data_provider;

import android.content.Context;
import android.content.SharedPreferences;
import mobile.seouling.com.application.BaseApplication;

public class BasePreference {

    private static final String TAG = "BasePreference";
    private static final String IS_SIGNED_IN = "is_signed_in";

    private static SharedPreferences sPref = null;

    private static SharedPreferences getInstance() {
        if (sPref == null) {
            synchronized (BasePreference.class) {
                if (sPref == null) {
                    sPref = BaseApplication.getContext().getSharedPreferences("basePref", Context.MODE_PRIVATE);
                }
            }
        }
        return sPref;
    }

    public static void setIsSignedIn(Boolean isSigned) {
        SharedPreferences.Editor edit = getInstance().edit();
        edit.putBoolean(IS_SIGNED_IN, isSigned);
        edit.apply();
    }

    public static Boolean isSignedIn() {
        return getInstance().getBoolean(IS_SIGNED_IN, false);
    }
}
