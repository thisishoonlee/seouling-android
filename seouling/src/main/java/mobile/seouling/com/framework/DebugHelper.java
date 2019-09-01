package mobile.seouling.com.framework;


import android.content.Context;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;

import java.lang.ref.WeakReference;
import java.util.Set;

public class DebugHelper {

    public static final String TAG = "MallangDebug";
    public static final String TAG_MEMORY = "MallngMemory";
    public static final String TAG_NETWORK = "MallangNetwork";

    private static final boolean DEBUG_METHOD_TRACE = false;

    private static Handler sHandler;

    public static String getMemoryStatText() {
        final float free = Runtime.getRuntime().freeMemory() / 1024f / 1024f;
        final float total = Runtime.getRuntime().totalMemory() / 1024f / 1024f;
        final float max = Runtime.getRuntime().maxMemory() / 1024f / 1024f;
        final float using = total - free;
        return String.format("Using=%.2f, Free=%03.2f, Total=%.2f, Max=%.2f", using, free, total, max);
    }

    public static void showMemoryStat(Context context) {
        if (sHandler == null) {
            sHandler = new Handler(Looper.getMainLooper());
        }

        final WeakReference<Context> contextRef = new WeakReference<>(context);
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                final Context context = contextRef.get();
                if (context != null) {
                    //Toast.makeText(context, memMessage, Toast.LENGTH_SHORT).show();
                    Log.v(TAG_MEMORY, getMemoryStatText());
                    sHandler.postDelayed(this, 2000);
                }
            }
        });
    }

    public static void printStackTrace() {
        StringBuilder string = new StringBuilder();
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 4; i < stackTrace.length; i++) {
            string.append('\n').append(stackTrace[i].toString());
        }
        Log.d(TAG, string.append('\n').toString());
    }

    public static void printStackTrace(int maxLength) {
        StringBuilder string = new StringBuilder();
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int length = Math.min(stackTrace.length, maxLength + 1);
        for (int i = 4; i < length; i++) {
            string.append('\n').append(stackTrace[i].toString());
        }
        Log.d(TAG, string.append('\n').toString());
    }

    private static int sPrintCount = 0;

    public static void printTreadNamesPeriodically() {
        if (sHandler == null) {
            sHandler = new Handler(Looper.getMainLooper());
        }

        sHandler.post(new Runnable() {
            @Override
            public void run() {
                Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                StringBuilder builder = new StringBuilder();
                for (Thread thread : threadSet) {
                    builder.append(thread.getId()).append(" : ").append(thread.getName()).append('\n');
                }
                Log.d(TAG, builder.toString());
                sPrintCount = Math.min(5, sPrintCount + 1);
                sHandler.postDelayed(this, 1000 + sPrintCount * 1500);
            }
        });
    }

    public static void startMethodTracing(String tag) {
        if (DEBUG_METHOD_TRACE) {
            Debug.startMethodTracing(tag, 80 * 1024 * 1024);
        }
    }

    public static void stopMethodTracing() {
        if (DEBUG_METHOD_TRACE) {
            Debug.stopMethodTracing();
        }
    }
}

