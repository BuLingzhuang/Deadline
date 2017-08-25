package com.bulingzhuang.deadline.interfaces.presenters

import android.content.Context
import com.bulingzhuang.deadline.interfaces.views.MainView

/**
 * Created by bulingzhuang
 * on 2017/8/24
 * E-mail:bulingzhuang@foxmail.com
 */
interface MainPresenter {
    /**
     * 获取天气数据
     */
    fun getWeatherData(context: Context)

    /**
     * 销毁页面操作
     */
    fun doBeforeDestroy()
}