package mobile.seouling.com.application.network

import mobile.seouling.com.BuildEnv
import okhttp3.Interceptor
import okhttp3.Response

class UserAgentInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.run {
            proceed(request()
                .newBuilder()
                .header("User-Agent", BuildEnv.USER_AGENT)
                .build())
        }
    }

}