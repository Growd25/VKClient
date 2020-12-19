package com.growd25.vkclient.utils

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun Disposable.connect(compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(this)
}
