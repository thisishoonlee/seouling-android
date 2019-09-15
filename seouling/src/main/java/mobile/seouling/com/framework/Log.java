package mobile.seouling.com.framework;

import android.content.Context;
import mobile.seouling.com.BuildEnv;
import mobile.seouling.com.application.data_provider.BasePreference;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Log {

    public static final String TAG = "Mallang";

    public static final int VERBOSE = android.util.Log.VERBOSE;
    public static final int DEBUG = android.util.Log.DEBUG;
    public static final int INFO = android.util.Log.INFO;
    public static final int WARN = android.util.Log.WARN;
    public static final int ERROR = android.util.Log.ERROR;
    public static final int ASSERT = android.util.Log.ASSERT;

    public static boolean isLoggable(String tag) {
        try {
            return android.util.Log.isLoggable(tag, DEBUG);
        } catch (Throwable e) {
            return false;
        }
    }

    public static boolean isLoggable(String tag, int level) {
        return android.util.Log.isLoggable(tag, level);
    }

    public static void v(String log) {
        if (BuildEnv.DEBUG) {
            android.util.Log.v(TAG, "" + log);
        }
    }

    public static void v(String tag, String msg) {
        if (BuildEnv.DEBUG) {
            android.util.Log.v(tag, msg);
        }
    }

    public static void d(String log) {
        if (BuildEnv.DEBUG) {
            android.util.Log.d(TAG, "" + log);
        }
    }

    public static void d(String tag, String msg) {
        if (BuildEnv.DEBUG) {
            android.util.Log.d(tag, msg);
        }
    }


    public static void i(String tag, String msg) {
        if (BuildEnv.DEBUG) {
            android.util.Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (BuildEnv.DEBUG) {
            android.util.Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (BuildEnv.DEBUG) {
            android.util.Log.w(tag, msg, tr);
        }
    }

    public static void e(String log) {
        e(TAG, log);
    }

    public static void e(String tag, String msg) {
        android.util.Log.e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        android.util.Log.e(tag, msg, tr);
    }

    public static void wtf(String tag, String msg, Throwable tr) {
        if (BuildEnv.DEBUG) {
            android.util.Log.wtf(tag, msg, tr);
            throw new RuntimeException(tr);
        }
    }

    public static void wtf(String tag, String msg) {
        if (BuildEnv.DEBUG) {
            android.util.Log.wtf(tag, msg);
        }
    }

    public static void logNetworkError(String id, Throwable tr) {
        android.util.Log.w(TAG, "id: " + id + " / " + tr.getMessage(), tr);
        //if (!BuildEnv.DEBUG || BasePreference.isFabricLogChecked()) {
        if (!BuildEnv.DEBUG) {
            CrashHelper.log(android.util.Log.ERROR, "NETWORK_ERROR", id, tr);
        }
    }

    public static void appendToFile(Context context, String filename, String text) {
        File logFile = new File(context.getExternalCacheDir(), filename);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                Log.e(filename, "appendToFile createNewFile error", e);
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            Log.e(filename, "appendToFile append error", e);
        }
    }
}
