package mobile.seouling.com.application.home.plan

import io.reactivex.Completable
import io.reactivex.processors.BehaviorProcessor

class PlanRepository(val local: PlanLocalDataSource, val remote: PlanRemoteDataSource) {

    private val isInitialLoadingProcessor = BehaviorProcessor.createDefault(false)
    private val isMoreLoadingProcessor = BehaviorProcessor.createDefault(false)

    private var isFinished = false
    private var after: String? = null

    private fun loadInitial(): Completable {
        return if(isFinished) {
            Completable.complete().doFinally{
                isInitialLoadingProcessor.onNext(false)
            }
        } else {
            remote
        }
    }
}