package com.bulingzhuang.deadline.bean

import android.graphics.Color
import com.bulingzhuang.deadline.views.adapters.DeadlineModelAdapter

/**
 * Created by bulingzhuang
 * on 2017/8/29
 * E-mail:bulingzhuang@foxmail.com
 */
class DeadlineModel constructor(var _id: Long,
                                var content: String,
                                var type: Type,
                                var startTime: Long,
                                var endTime: Long,
                                var startColor: String,
                                var endColor: String,
                                var showStatus: ShowStatus = ShowStatus.OPEN) {
    enum class Type(val typeName: String) {
        FESTIVAL("节日"),
        BIRTHDAY("生日"),
        WORK("工作"),
        FAMILY("家庭"),
        OTHER("其他")
    }

    enum class ShowStatus {
        OPEN, CLOSE, EMPTY
    }

    override fun toString(): String {
        return "_id=$_id,content=$content,type=${type.name},startTime=$startTime,endTime=$endTime,startColor=$startColor,endColor=$endColor"
    }
}