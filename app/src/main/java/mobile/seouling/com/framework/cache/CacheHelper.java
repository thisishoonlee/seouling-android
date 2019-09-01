package mobile.seouling.com.framework.cache;

import android.content.Context;
import android.os.StatFs;
import java.io.File;

public class CacheHelper {

    private static final String TAG = "CacheHelper";

    private static final int MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final int MAX_DISK_CACHE_SIZE = 200 * 1024 * 1024; // 200MB

    public static File createCacheDir(Context context, String dir) {
        File cache = new File(context.getCacheDir(), dir);
        if (!cache.exists()) {
            cache.mkdirs();
        }
        return cache;
    }

    @SuppressWarnings("deprecation")
    public static long calculateDiskCacheSize(File dir) {
        long size = MIN_DISK_CACHE_SIZE;

        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            long available = ((long) statFs.getBlockCount()) * statFs.getBlockSize();
            // Target 2% of the total space.
            size = available / 50;
        } catch (IllegalArgumentException ignored) {
        }

        // Bound inside min/max size for disk cache.
        return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);
    }
}

