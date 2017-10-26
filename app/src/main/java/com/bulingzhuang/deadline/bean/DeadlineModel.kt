package com.bulingzhuang.deadline.bean

import java.io.Serializable

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
                                var showStatus: ShowStatus = ShowStatus.OPEN) : Serializable {
    enum class Type(val typeName: String) {
        FESTIVAL("节日"),
        BIRTHDAY("生日"),
        WORK("工作"),
        FAMILY("家庭"),
        OTHER("其他")
    }

    enum class ShowStatus {
        OPEN, CLOSE, VALID
    }

    override fun toString(): String {
        return "_id=$_id,content=$content,type=${type.typeName},startTime=$startTime,endTime=$endTime"
    }
}