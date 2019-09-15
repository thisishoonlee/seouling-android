package mobile.seouling.com.application.data.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Flowable
import mobile.seouling.com.application.data.vo.Plan

@Dao
interface PlanDao : BaseDao<Plan>
{
    @Query("SELECT * FROM plans")
    fun getPlans() : Flowable<List<Plan>>

    @Query("SELECT * FROM plans")
    fun getPlansAsDataSourceFactory(): DataSource.Factory<Int, Plan>
}