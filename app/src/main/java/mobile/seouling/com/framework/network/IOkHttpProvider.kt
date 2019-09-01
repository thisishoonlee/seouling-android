package mobile.seouling.com.framework.network

import okhttp3.OkHttpClient

interface IOkHttpProvider {
    //val imageLoader: OkHttpClient
    val mallangApi: OkHttpClient
}