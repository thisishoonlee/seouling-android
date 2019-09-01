package mobile.seouling.com.framework;

import android.os.Handler;
import android.os.Looper;
import mobile.seouling.com.framework.rx.MainThreadScheduler;

public class ThreadHelper {
    private static final Handler sMainThreadHandler = new Handler(Looper.getMainLooper());
    private static MainThreadScheduler sMainThreadScheduler;

    public static Handler mainHandler() {
        return sMainThreadHandler;
    }

    public static MainThreadScheduler mainScheduler() {
        if (sMainThreadScheduler == null) {
            synchronized (MainThreadScheduler.class) {
                if (sMainThreadScheduler == null) {
                    sMainThreadScheduler = new MainThreadScheduler(sMainThreadHandler);
                }
            }
        }
        return sMainThreadScheduler;
    }
}
