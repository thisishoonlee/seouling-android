package mobile.seouling.com.application.api

import io.reactivex.Flowable
import io.reactivex.Single
import mobile.seouling.com.application.data.vo.Plan
import mobile.seouling.com.application.data.vo.Schedule
import mobile.seouling.com.application.data.vo.Tag
import mobile.seouling.com.application.json.Response
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PlanService {

    @GET("plan")
    fun getPlans(@Query("last_id") lastId: Int? = null): Flowable<Response<List<Plan>>>

    // "start_date": String,
    //  "end_date": String,
    //  "name": String
    @POST("plan")
    fun createPlan(@Body body: RequestBody): Single<Response<Plan>>

    @GET("plan/{plan_id}")
    fun getPlan(@Path("plan_id") planId: Int): Single<Response<Plan>>

    //"name": String(Optional),
    //  "start_date": String(Optional),
    //  "end_date": String(Optional),
    @PUT("plan/{plan_id}")
    fun editPlan(@Path("plan_id") planId: Int, @Body body: RequestBody): Single<Response<Plan>>

    @DELETE("plan/{plan_id}")
    fun editPlan(@Path("plan_id") planId: Int): Single<Response<Any>>

    @GET("plan/{plan_id}/schedule")
    fun getSchedule(@Path("plan_id") planId: Int): Single<Response<Schedule>>

    //{
    //  "add": List(spot ids),
    //  "delete": List(spot ids)
    //}
    @PUT("schedule/{schedule_id}")
    fun editSchedule(@Path("schedule_id") scheduleId: Int,
                     @Query("type") type: String, @Body body: RequestBody
    ): Single<Response<Schedule>>

    @GET("tag")
    fun getTags(): Single<Response<List<Tag>>>
}

enum class ScheduleType(type: String) {
    MORNING("morning"), AFTERNOON("after_noon"), NIGHT("night")
}