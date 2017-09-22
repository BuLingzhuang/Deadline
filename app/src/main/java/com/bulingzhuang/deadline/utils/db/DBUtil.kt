package com.bulingzhuang.deadline.utils.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

/**
 * Created by bulingzhuang
 * on 2017/8/29
 * E-mail:bulingzhuang@foxmail.com
 */
class DBUtil(context: Context) : ManagedSQLiteOpenHelper(context, DB_NAME) {

    companion object {
        val DB_NAME = "deadline_db"
        val TABLE_NAME_deadline = "deadline"
        val DEADLINE_id = "_id"
        val DEADLINE_content = "content"
        val DEADLINE_type = "type"
        val DEADLINE_startTime = "startTime"
        val DEADLINE_endTime = "endTime"
        val DEADLINE_startColor = "Args_startColor"
        val DEADLINE_endColor = "endColor"
        val DEADLINE_textColor = "textColor"
        val DEADLINE_isGradient = "isGradient"

        private var instance: DBUtil? = null

        @Synchronized
        fun getInstance(context: Context): DBUtil {
            if (instance == null) {
                instance = DBUtil(context.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.createTable(TABLE_NAME_deadline, false,
                DEADLINE_id to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                DEADLINE_content to TEXT,
                DEADLINE_type to TEXT,
                DEADLINE_startTime to INTEGER,
                DEADLINE_endTime to INTEGER,
                DEADLINE_startColor to TEXT,
                DEADLINE_endColor to TEXT,
                DEADLINE_textColor to TEXT,
                DEADLINE_isGradient to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}