package mobile.seouling.com.application

import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo

open class BaseViewModel(private val schedulers: BaseSchedulerProvider) : ViewModel() {
    private val disposables by lazy { CompositeDisposable() }

    final override fun onCleared() {
        disposables.clear()
        onViewModelCleared()
    }

    protected open fun onViewModelCleared() {}

    protected fun <T : Any> Flowable<T>.subscribeOnIO(): Flowable<T> = subscribeOn(schedulers.io())
    protected fun <T : Any> Single<T>.subscribeOnIO(): Single<T> = subscribeOn(schedulers.io())
    protected fun <T : Any> Maybe<T>.subscribeOnIO(): Maybe<T> = subscribeOn(schedulers.io())
    protected fun Completable.subscribeOnIO(): Completable = subscribeOn(schedulers.io())

    protected fun Disposable.addToDisposables(): Disposable = addTo(disposables)
}
