package mobile.seouling.com.application.network

import android.content.Context
import mobile.seouling.com.BuildEnv
import mobile.seouling.com.framework.Log
import mobile.seouling.com.framework.cache.CacheHelper
import mobile.seouling.com.framework.network.IOkHttpProvider
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.*

class OkHttpProviderImpl(context: Context) : IOkHttpProvider {
    //private val apiInterceptor: Interceptor = MallangNetworkInterceptor(DeviceHelper.getUuid(context))
    private val errorInterceptor: Interceptor =
        BaseNetworkErrorResponseInterceptor()
    private val userAgentInterceptor: Interceptor = UserAgentInterceptor()
    private val logInterceptor: Interceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
    }
    private val cache = newCache(context)

    override val mallangApi: OkHttpClient = OkHttpClient.Builder()
        .apply {
            addInterceptor(userAgentInterceptor)
            addInterceptor(errorInterceptor)
            if (BuildEnv.DEBUG) {
                addInterceptor(logInterceptor)
            }
        }.cache(cache)
        .build()

    private fun newCache(context: Context): Cache {
        val cacheDir = CacheHelper.createCacheDir(context,
            CACHE_DIR
        )
        val maxSize = CacheHelper.calculateDiskCacheSize(cacheDir)
        val cache = Cache(cacheDir, maxSize)
        try {
            val size = String.format("%.1fM", cache.size().toFloat() / 1000000f)
            Log.v(TAG, String.format(Locale.ENGLISH, "okhttp3 cache size: %sB", size))
        } catch (e: IOException) {
            Log.wtf(TAG, "okhttp3 cache size: unknown", e)
        }

        return cache
    }

    companion object {
        private const val TAG = "OkHttp"
        private const val CACHE_DIR = "okhttp3-cache"
    }
}