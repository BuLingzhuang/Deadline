package com.bulingzhuang.deadline.impl.interactors

import com.bulingzhuang.deadline.utils.net.BaseObserver
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by bulingzhuang
 * on 2017/8/24
 * E-mail:bulingzhuang@foxmail.com
 */
open class BaseInteractorImpl {

    private val mCompositeDisposable: CompositeDisposable = CompositeDisposable()

    open fun doBeforeDestroy() {
        if (mCompositeDisposable.isDisposed) {
            mCompositeDisposable.dispose()
        }
    }

    fun <BLZ> addSubscription(observable: Observable<BLZ>, observer: BaseObserver<BLZ>) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
        mCompositeDisposable.add(observer.disposable)
    }
}