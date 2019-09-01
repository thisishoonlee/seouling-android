package mobile.seouling.com.framework

import com.google.gson.Gson
import com.google.gson.GsonBuilder


object GsonFactory {
    private val _gson: Gson by lazy {
        GsonBuilder()
            .create()
    }

    @JvmStatic
    fun getGson(): Gson {
        return _gson
    }

    @JvmStatic
    val debugGson: Gson by lazy {
        GsonBuilder().setPrettyPrinting().create()
    }
}
