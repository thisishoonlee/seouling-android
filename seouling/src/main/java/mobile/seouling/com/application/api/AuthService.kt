package mobile.seouling.com.application.api

import io.reactivex.Single
import mobile.seouling.com.application.data.vo.Auth
import mobile.seouling.com.application.json.Response
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthService {

    // body : token: String
    @POST("auth/token")
    fun setToken(@Body body: RequestBody):Single<Response<String>>

//        "sns_token": String(Optional),
//        "email": String(Optional),
//        "password": String(Optional),
//        "login_type": String,
//        "nickname": String,
//        "profile_picture": String(Optional)
    @POST("auth/signup")
    fun signUp(@Body body: RequestBody): Single<Response<Auth>>

    // email : String
    // password : String
    @POST("auth/signin/email")
    fun signInByEmail(@Body body: RequestBody) : Single<Response<String>>

    // sns_token: String
    // type: String
    @POST("auth/signin/sns")
    fun signInBySNS(@Body body: RequestBody): Single<Response<String>>

}