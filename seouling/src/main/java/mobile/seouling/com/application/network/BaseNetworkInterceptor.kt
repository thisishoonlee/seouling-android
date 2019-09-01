package mobile.seouling.com.application.network

//
//class MallangNetworkInterceptor : Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val request = chain.request()
//        val builder = request.newBuilder()
//        (request to builder).apply {
//            val auth = AuthModel.get()
//            if (auth != null) {
//                addHeader("X-Mallang-Authentication-Token", auth.token)
//            }
//            addHeader("X-Mallang-UDID", deviceUuid)
//            addHeader("Accept-Language", BuildEnv.LANGUAGE)
//            addHeader("X-Mallang-Session-Key", SessionManager.instance.sessionId)
//            if (!TextUtils.isEmpty(BuildConfig.X_VINGLE_APPLICATION_ID)) {
//                addHeader("X-Mallang-Application-Id", BuildConfig.X_VINGLE_APPLICATION_ID)
//            }
//            if (!TextUtils.isEmpty(BuildConfig.X_VINGLE_REST_API_TOKEN)) {
//                addHeader("X-Mallang-Rest-Api-Token", BuildConfig.X_VINGLE_REST_API_TOKEN)
//            }
//            if (request.method().toUpperCase() != "GET" && request.body() !is MultipartBody) {
//                addHeader("Content-Type", "application/json; charset=utf-8")
//            }
//            addHeader("Accept", BuildEnv.DEFAULT_API_VERSION)
//        }
//        return chain.proceed(builder.build())
//    }
//
//    private fun Pair<Request, Request.Builder>.addHeader(key: String, value: String?) {
//        val request = first
//        val builder = second
//        if (first.header(key) != null) {
//            return
//        }
//        if (value == null) {
//            Log.wtf(TAG, "value is null. key: $key, request: $request")
//            return
//        }
//        builder.addHeader(key, value)
//    }
//
//    companion object {
//        private const val TAG = "VNetworkInterceptor"
//    }
//}