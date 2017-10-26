package com.bulingzhuang.deadline.impl.interactors

import android.text.TextUtils
import com.bulingzhuang.deadline.bean.WeatherModel
import com.bulingzhuang.deadline.interfaces.interactors.MainInteractor
import com.bulingzhuang.deadline.utils.net.ApiClient
import com.bulingzhuang.deadline.utils.net.BaseObserver

/**
 * Created by bulingzhuang
 * on 2017/8/24
 * E-mail:bulingzhuang@foxmail.com
 */
class MainInteractorImpl : BaseInteractorImpl(), MainInteractor {

    override fun getWeatherData(observer: BaseObserver<WeatherModel>, city: String) {
        if (TextUtils.isEmpty(city)) {
            addSubscription(ApiClient.retrofit().loadWeather(), observer)
        } else {
            addSubscription(ApiClient.retrofit().loadWeather(city), observer)
        }
    }
}