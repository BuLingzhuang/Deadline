package com.bulingzhuang.deadline.utils

import android.content.Context
import com.bulingzhuang.deadline.bean.TypeColorModel
import com.google.gson.JsonParser
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by bulingzhuang
 * on 2017/9/29
 * E-mail:bulingzhuang@foxmail.com
 */
object Tools {


    private val hourPara = 1000 * 60 * 60

    /**
     * 计算剩余时间
     */
    fun computeTime(sDate: Long, eDate: Long): Pair<Int, Int> {
        val remainingD = eDate - sDate
        val rDay = remainingD / hourPara / 24
        val rHour = remainingD / hourPara - rDay * 24
        return Pair(rDay.toInt(), rHour.toInt())
    }

    /**
     * 时间毫秒数转换日期(yyyy-MM-dd)
     */
    fun formatMillis2Str(time: Long): Pair<String, Int> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        val year = calendar.get(Calendar.YEAR).toString()
        val month = switchStr(calendar.get(Calendar.MONTH), true)
        val day = switchStr(calendar.get(Calendar.DAY_OF_MONTH))
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        return Pair(year + month + day, hour)
    }

    /**
     * 月、日转换str
     */
    fun switchStr(int: Int, needAdd: Boolean = false): String {
        val result: Int = if (needAdd) {
            int + 1
        } else {
            int
        }
        return if (result < 10) {
            "-0" + result.toString()
        } else {
            "-" + result.toString()
        }
    }

    /**
     * 比较颜色模块是否有样式变更
     */
    fun compareColor(lastModel: TypeColorModel.ColorModel, currentModel: TypeColorModel.ColorModel): Boolean {
        if (lastModel.isGradient != currentModel.isGradient) {
            return false
        }
        if (lastModel.contentColor != currentModel.contentColor) {
            return false
        }
        if (lastModel.endColor != currentModel.endColor) {
            return false
        }
        return true
    }

    fun readAssets(context: Context, fileName: String): String {
        val sb = StringBuilder()
        val inputStream = context.assets.open(fileName)
        val buffer = ByteArray(1024)
        while (true){
            val readLength = inputStream.read(buffer)
//            showLogE("读了$readLength 个")
            if (readLength == -1){
                break
            }
            val string = String(buffer,0,readLength)
//            showLogE("读入数据：$string")
            sb.append(string)
        }
        inputStream.close()
        return sb.toString()
    }
}