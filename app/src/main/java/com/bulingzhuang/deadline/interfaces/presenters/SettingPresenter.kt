package com.bulingzhuang.deadline.interfaces.presenters

import android.animation.AnimatorSet
import com.bulingzhuang.deadline.views.ui.CircleProgressView

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
}