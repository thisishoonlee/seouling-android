package mobile.seouling.com.framework.compat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by ShinYoung on 2014. 8. 7..
 */
public final class AlertDialogCompat {
    public static final int THEME_HOLO_DARK = 2;
    public static final int THEME_DEVICE_DEFAULT_LIGHT = 5;

    @SuppressLint("NewApi")
    public static AlertDialog.Builder newBuilder(Context context, int theme) {
        return new AlertDialog.Builder(context, theme);
    }
}
