package com.bulingzhuang.deadline.interfaces.views

import com.bulingzhuang.deadline.bean.TypeColorModel

/**
 * Created by bulingzhuang
 * on 2017/11/1
 * E-mail:bulingzhuang@foxmail.com
 */
interface SettingView {

    /**
     * 隐藏查询功能
     */
    fun hideSearchFunction(city:String = "")

    /**
     * 修改默认颜色
     */
    fun setDefaultColor(model: TypeColorModel.ColorModel, position: Int, rotateValue: Float = -180f)
}