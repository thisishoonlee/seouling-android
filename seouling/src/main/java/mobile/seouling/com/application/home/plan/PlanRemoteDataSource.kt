package mobile.seouling.com.application.home.plan

import io.reactivex.Flowable
import mobile.seouling.com.application.api.PlanService
import mobile.seouling.com.application.data.vo.Plan

class PlanRemoteDataSource(private val service: PlanService) {

    fun getPlans(after: Int? = null): Flowable<List<Plan>> {
        after?.let {
            service.getPlans(it)
        } ?: service.getPlans()
                .compose()
    }
}