package mobile.seouling.com.application.network

import mobile.seouling.com.framework.Log
import okhttp3.Interceptor
import okhttp3.Response
import javax.net.ssl.SSLHandshakeException

class BaseNetworkErrorResponseInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: SSLHandshakeException) {
            Log.i("ErrorInterceptor", "request ssl handshake failure $request")
            throw e
        } catch (e: Exception) {
            Log.i("ErrorInterceptor", "request failure $request")
            throw e
        }

        if (response.code() >= 500) {
            val requestId = response.header("X-Request-Id")
            val body = response.body()
            val bodyErrorMessage: String
            if (body != null) {
                bodyErrorMessage = response.peekBody(100).string()
            } else {
                bodyErrorMessage = "empty body"
            }
            log(requestId, RuntimeException(bodyErrorMessage))
        }
        return response
    }

    private fun log(requestId: String?, error: Throwable) {
        requestId ?: return
        Log.logNetworkError(requestId, error)
    }
}