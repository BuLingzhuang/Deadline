package com.bulingzhuang.deadline.interfaces.presenters

import android.animation.AnimatorSet
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.widget.EditText
import com.bulingzhuang.deadline.views.ui.CircleProgressView
import com.bulingzhuang.deadline.views.ui.GrayQuickTipsView

/**
 * Created by bulingzhuang
 * on 2017/10/25
 * E-mail:bulingzhuang@foxmail.com
 */
interface SettingPresenter {

    /**
     * 初始化默认颜色显隐动画
     */
    fun initAnimSet(vararg views:CircleProgressView): AnimatorSet

    /**
     * 初始化选择城市Adapter
     */
    fun initDefaultCityAdapter(context:Context,view:GrayQuickTipsView,etView: EditText)
}