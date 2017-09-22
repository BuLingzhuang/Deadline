package com.bulingzhuang.deadline.interfaces.presenters

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import com.bulingzhuang.deadline.bean.DeadlineModel
import com.bulingzhuang.deadline.views.fragments.ContentDialogFragment

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
    fun initAdapter(context: Context, recyclerView: RecyclerView)

    /**
     * 检查Adapter是否需要刷新
     */
    fun checkAdapter()

    /**
     * 显示新增/编辑对话框
     */
    fun showDialog(context: AppCompatActivity, data:DeadlineModel?)

    /**
     * 添加一条数据
     */
    fun insertItem(context: Context, content: String, typeName:String, startTime: Long, endTime: Long, textColor: String, startColor: String, endColor: String,isGradient:Boolean)

    /**
     * 销毁页面操作
     */
    fun doBeforeDestroy()
}