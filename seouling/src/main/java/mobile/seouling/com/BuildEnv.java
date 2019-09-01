package mobile.seouling.com;

import android.os.Build;

import java.util.Locale;

/**
 * Build Options
 */
public class BuildEnv {

    public static boolean DEBUG = BuildConfig.DEBUG;
    public static boolean TEST = false; // set by buildScript
    public static boolean STRICT_MODE = false && DEBUG;
    public static boolean MIMIC_UNSTABLE_NETWORK = false;
    public static final boolean RESET_PASSWORD_VIA_WEB = false;
    public static final boolean DEBUG_METHOD_TRACE = false && DEBUG;

    public static final String USER_AGENT = String.format(
            Locale.US,
            "Android/%d (%s; android %s)",
            BuildConfig.VERSION_CODE, Build.MODEL, Build.VERSION.RELEASE);

    public static final String LANGUAGE = Locale.getDefault().getLanguage();

    public static void initialize(boolean debug, boolean test) {
        BuildEnv.DEBUG = debug;
        BuildEnv.TEST = test;
    }
}
