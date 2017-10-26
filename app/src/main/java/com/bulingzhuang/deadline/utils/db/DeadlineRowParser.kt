package com.bulingzhuang.deadline.utils.db

import com.bulingzhuang.deadline.bean.DeadlineModel
import org.jetbrains.anko.db.RowParser

/**
 * Created by bulingzhuang
 * on 2017/8/30
 * E-mail:bulingzhuang@foxmail.com
 */
class DeadlineRowParser : RowParser<DeadlineModel> {
    override fun parseRow(columns: Array<Any?>): DeadlineModel {
        val type = when (columns[2]) {
            DeadlineModel.Type.BIRTHDAY.typeName -> {
                DeadlineModel.Type.BIRTHDAY
            }
            DeadlineModel.Type.WORK.typeName -> {
                DeadlineModel.Type.WORK
            }
            DeadlineModel.Type.FESTIVAL.typeName -> {
                DeadlineModel.Type.FESTIVAL
            }
            DeadlineModel.Type.FAMILY.typeName -> {
                DeadlineModel.Type.FAMILY
            }
            else -> {
                DeadlineModel.Type.OTHER
            }
        }
        //        showLogE("_id=${columns[0]},$deadlineModel")
        return DeadlineModel(columns[0] as Long, columns[1] as String, type, columns[3] as Long, columns[4] as Long)
    }
}