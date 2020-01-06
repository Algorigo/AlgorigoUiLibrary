package com.algorigo.platform.algorigouilibrary.dialog

import io.reactivex.CompletableObserver
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

fun <T> Observer<T>.toCompletableObserver(): CompletableObserver {
    return object : CompletableObserver {
        override fun onSubscribe(d: Disposable) {
            this@toCompletableObserver.onSubscribe(d)
        }

        override fun onComplete() {
            this@toCompletableObserver.onComplete()
        }

        override fun onError(e: Throwable) {
            this@toCompletableObserver.onError(e)
        }
    }
}