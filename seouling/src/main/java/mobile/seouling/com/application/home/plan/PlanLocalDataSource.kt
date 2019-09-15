package mobile.seouling.com.application.home.plan

import io.reactivex.Flowable
import mobile.seouling.com.application.data.db.PlanDao
import mobile.seouling.com.application.data.vo.Plan

class PlanLocalDataSource(val planDao: PlanDao) {

    fun getPlans(): Flowable<List<Plan>> = planDao.getPlans()

    fun getPlanDataSourceFactory() = planDao.getPlansAsDataSourceFactory()
}