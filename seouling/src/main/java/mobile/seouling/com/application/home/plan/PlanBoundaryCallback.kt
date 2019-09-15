package mobile.seouling.com.application.home.plan

import androidx.paging.PagedList
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import mobile.seouling.com.application.data.vo.Plan
import mobile.seouling.com.framework.rx.kt.subscribeWithErrorConsumer

class PlanBoundaryCallback(
        private val loadInitial: () -> Completable,
        private val loadAfter: () -> Completable
) : PagedList.BoundaryCallback<Plan>() {

    private var initialDisposable: Disposable? = null
    private var afterDisposable: Disposable? = null

    override fun onZeroItemsLoaded() {
        if (initialDisposable?.isDisposed == false) return
        loadInitial()
                .subscribeWithErrorConsumer()
                .also { initialDisposable = it }
    }

    override fun onItemAtEndLoaded(itemAtEnd: Plan) {
        if (afterDisposable?.isDisposed == false) return
        loadAfter()
                .subscribeWithErrorConsumer()
                .also { afterDisposable = it }
    }
}