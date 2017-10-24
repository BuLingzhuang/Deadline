package com.bulingzhuang.deadline.bean

import java.io.Serializable

/**
 * Created by bulingzhuang
 * on 2017/10/23
 * E-mail:bulingzhuang@foxmail.com
 */
class TypeColorModel constructor(val typeList: ArrayList<ColorModel>):Serializable {

    class ColorModel constructor(val typeName: String, var isGradient: Boolean, var contentColor: String, var endColor: String):Serializable
}