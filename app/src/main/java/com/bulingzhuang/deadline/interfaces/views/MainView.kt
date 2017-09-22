package com.bulingzhuang.deadline.interfaces.views

import com.bulingzhuang.deadline.bean.WeatherModel

/**
 * Created by bulingzhuang
 * on 2017/8/24
 * E-mail:bulingzhuang@foxmail.com
 */
interface MainView {
    /**
     * 更新天气信息
     */
    fun updateWeather(data: WeatherModel.ResultsBean.NowBean,showAnim:Boolean = true)

    /**
     * 返回错误信息
     */
    fun updateError(eText:String?)
}