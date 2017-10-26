package com.bulingzhuang.deadline.impl.presenters

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import com.bulingzhuang.deadline.interfaces.presenters.SettingPresenter
import com.bulingzhuang.deadline.views.ui.CircleProgressView

/**
 * Created by bulingzhuang
 * on 2017/10/25
 * E-mail:bulingzhuang@foxmail.com
 */
class SettingPresenterImpl : SettingPresenter {

    /**
     * 初始化默认颜色显隐动画
     */
    override fun initAnimSet(vararg views: CircleProgressView): AnimatorSet {
        val set = AnimatorSet()
        val delayDuration = 233L
        val animList = ArrayList<Animator>()
        for (position in views.indices) {
            val itemView = views[position]
            val alphaAnim = ObjectAnimator.ofFloat(itemView, "alpha", 0f, 1f).setDuration(1000)
            val rotationYAnim = ObjectAnimator.ofFloat(itemView, "rotationY", -180f, 0f).setDuration(1000)
            alphaAnim.startDelay = position % 3 * delayDuration
            rotationYAnim.startDelay = position % 3 * delayDuration
            animList.add(alphaAnim)
            animList.add(rotationYAnim)
        }
        set.playTogether(animList)
        return set
    }
}