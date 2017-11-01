package com.bulingzhuang.deadline.interfaces.presenters

import android.animation.AnimatorSet
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.EditText
import com.bulingzhuang.deadline.bean.TypeColorModel
import com.bulingzhuang.deadline.views.ui.CircleProgressView
import com.bulingzhuang.deadline.views.ui.GrayQuickTipsView

/**
 * Created by bulingzhuang
 * on 2017/10/25
 * E-mail:bulingzhuang@foxmail.com
 */
interface SettingPresenter {

    /**
     * 设置默认颜色的点击监听
     */
    fun setCpvListener(activity: AppCompatActivity, vararg views: View)

    /**
     * 初始化默认颜色显隐动画
     */
    fun initAnimSet(vararg views:CircleProgressView): AnimatorSet

    /**
     * 初始化选择城市Adapter
     */
    fun initDefaultCityAdapter(context:Context,view:GrayQuickTipsView,etView: EditText)

    /**
     * 保存修改后的颜色
     */
    fun handleColorWithAnim(context: Context,view: CircleProgressView, model: TypeColorModel.ColorModel, position: Int, rotateValue: Float = -180f)

    /**
     * 取默认颜色
     */
    fun getDefaultColor(workView:CircleProgressView,festivalView:CircleProgressView,birthdayView:CircleProgressView,familyView:CircleProgressView,otherView:CircleProgressView)

    /**
     * 重置ColorModel
     */
    fun replaceAllColorModel()
}