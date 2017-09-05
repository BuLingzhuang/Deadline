package com.bulingzhuang.deadline.interfaces.presenters

import android.content.Context
import android.support.v7.widget.RecyclerView

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
     * 初始化Adapter
     */
    fun initAdapter(context: Context,recyclerView: RecyclerView)

    fun insertItem(context: Context,str:String)

    /**
     * 销毁页面操作
     */
    fun doBeforeDestroy()
}