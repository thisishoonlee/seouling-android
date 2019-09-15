package mobile.seouling.com.application.api

import io.reactivex.Single
import mobile.seouling.com.application.data.vo.Spot
import mobile.seouling.com.application.json.Response
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SpotService {

    // request: tags
    @POST("spot/search/tag")
    fun searchSpotByTag(@Query("last_id") lastId: Int, @Body body: RequestBody): Single<Response<List<Spot>>>

    // request: name
    @POST("spot/search/name")
    fun searchSpotByName(@Query("last_id") lastId: Int, @Body body: RequestBody): Single<Response<List<Spot>>>

    @GET("spot/{spot_id}")
    fun getSpot(@Query("spot_id") spot_id: Int): Single<Response<Spot>>

//    @GET("spot/{spot_id}/comment")
//    fun getSpotComments(@Query("last_id") lastId: Int):
}