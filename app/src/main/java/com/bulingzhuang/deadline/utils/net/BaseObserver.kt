package com.bulingzhuang.deadline.utils.net

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by bulingzhuang
 * on 2017/8/24
 * E-mail:bulingzhuang@foxmail.com
 */
abstract class BaseObserver<BLZ> : Observer<BLZ> {

    var disposable: Disposable? = null

    override fun onSubscribe(d: Disposable?) {
        disposable = d
    }
}