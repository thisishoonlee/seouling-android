package mobile.seouling.com.framework.rx

import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.ofType

sealed class RxEvent<T>
data class DataEvent<T>(val data: T) : RxEvent<T>()
data class ErrorEvent<T>(val error: Throwable) : RxEvent<T>()

fun <T, U> Single<T>.flatMapEvent(transform: (T) -> U): Single<out RxEvent<U>> {
    return this.map<RxEvent<U>> {
        DataEvent(
            transform(
                it
            )
        )
    }
            .onErrorReturn { ErrorEvent(it) }
}

fun <T> Single<T>.flatMapEvent(): Single<out RxEvent<T>> {
    return this.flatMapEvent { it }
}

fun <T> Observable<T>.flatMapEvent(): Observable<out RxEvent<T>> {
    return this.map<RxEvent<T>> { DataEvent(it) }
            .onErrorReturn { ErrorEvent(it) }
}

fun <T> Flowable<T>.flatMapEvent(): Flowable<out RxEvent<T>> {
    return this.map<RxEvent<T>> { DataEvent(it) }
            .onErrorReturn { ErrorEvent(it) }
}

fun <T> Flowable<out RxEvent<T>>.filterDataEvent(): Flowable<DataEvent<T>> {
    return this.ofType()
}

fun <T> Flowable<out RxEvent<T>>.filterErrorEvent(): Flowable<ErrorEvent<T>> {
    return this.ofType()
}

fun <T> Maybe<T>.flatMapEvent(): Maybe<out RxEvent<T>> {
    return this.map<RxEvent<T>> { DataEvent(it) }
            .onErrorReturn { ErrorEvent(it) }
}
