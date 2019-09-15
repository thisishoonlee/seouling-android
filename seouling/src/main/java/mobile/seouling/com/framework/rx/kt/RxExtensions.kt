package mobile.seouling.com.framework.rx.kt

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.annotations.BackpressureKind
import io.reactivex.annotations.BackpressureSupport
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.annotations.SchedulerSupport
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

private val onNextStub: (Any) -> Unit = {}
private val onCompleteStub: () -> Unit = {}

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
fun <T : Any> Observable<T>.subscribeWithErrorConsumer(onNext: (T) -> Unit = onNextStub): Disposable =
        subscribeBy(onNext = onNext, onError = ErrorConsumer()::invoke)

@CheckReturnValue
@BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
@SchedulerSupport(SchedulerSupport.NONE)
fun <T : Any> Flowable<T>.subscribeWithErrorConsumer(onNext: (T) -> Unit = onNextStub): Disposable =
        subscribeBy(onNext = onNext, onError = ErrorConsumer()::invoke)

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
fun <T : Any> Single<T>.subscribeWithErrorConsumer(onSuccess: (T) -> Unit = onNextStub): Disposable =
        subscribeBy(onSuccess = onSuccess, onError = ErrorConsumer()::invoke)

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
fun <T : Any> Maybe<T>.subscribeWithErrorConsumer(onSuccess: (T) -> Unit = onNextStub): Disposable =
        subscribeBy(onSuccess = onSuccess, onError = ErrorConsumer()::invoke)

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
fun Completable.subscribeWithErrorConsumer(onComplete: () -> Unit = onCompleteStub): Disposable =
        subscribeBy(onComplete = onComplete, onError = ErrorConsumer()::invoke)

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
fun <T : Any> Observable<T>.subscribeWithErrorConsumer(
        errorMessage: String = "",
        onNext: (T) -> Unit = onNextStub
): Disposable = subscribeBy(onNext = onNext, onError = ErrorConsumer(errorMessage)::invoke)

@CheckReturnValue
@BackpressureSupport(BackpressureKind.UNBOUNDED_IN)
@SchedulerSupport(SchedulerSupport.NONE)
fun <T : Any> Flowable<T>.subscribeWithErrorConsumer(
        errorMessage: String = "",
        onNext: (T) -> Unit = onNextStub
): Disposable = subscribeBy(onNext = onNext, onError = ErrorConsumer(errorMessage)::invoke)

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
fun <T : Any> Single<T>.subscribeWithErrorConsumer(
        errorMessage: String = "",
        onSuccess: (T) -> Unit = onNextStub
): Disposable = subscribeBy(onSuccess = onSuccess, onError = ErrorConsumer(errorMessage)::invoke)

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
fun <T : Any> Maybe<T>.subscribeWithErrorConsumer(
        errorMessage: String = "",
        onSuccess: (T) -> Unit = onNextStub
): Disposable = subscribeBy(onSuccess = onSuccess, onError = ErrorConsumer(errorMessage)::invoke)

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
fun Completable.subscribeWithErrorConsumer(
        errorMessage: String = "",
        onComplete: () -> Unit = onCompleteStub
): Disposable = subscribeBy(onComplete = onComplete, onError = ErrorConsumer(errorMessage)::invoke)