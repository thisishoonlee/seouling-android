package mobile.seouling.com.application

import androidx.annotation.CallSuper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import mobile.seouling.com.application.common.fragment.BaseFragment

open class BaseViewModelFragment : BaseFragment() {

    private val disposables by lazy { CompositeDisposable() }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    protected fun Disposable.addToDisposables(): Disposable = addTo(disposables)
}