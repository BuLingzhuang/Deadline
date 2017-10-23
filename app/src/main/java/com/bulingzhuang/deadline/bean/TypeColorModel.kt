package com.bulingzhuang.deadline.bean

/**
 * Created by bulingzhuang
 * on 2017/10/23
 * E-mail:bulingzhuang@foxmail.com
 */
class TypeColorModel constructor(var typeList: MutableList<ColorModel>) {

    class ColorModel constructor(val typeName: String, var isGradient: Boolean, var contentColor: String, var endColor: String)
}