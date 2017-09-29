package com.bulingzhuang.deadline.utils

import java.util.*

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
}