package com.bulingzhuang.deadline.interfaces.interactors

import com.bulingzhuang.deadline.bean.WeatherModel
import com.bulingzhuang.deadline.utils.net.BaseObserver

/**
 * Created by bulingzhuang
 * on 2017/8/24
 * E-mail:bulingzhuang@foxmail.com
 */
interface MainInteractor {
    /**
     * 访问网络获取天气数据
     */
    fun getWeatherData(observer: BaseObserver<WeatherModel>,city:String)
}