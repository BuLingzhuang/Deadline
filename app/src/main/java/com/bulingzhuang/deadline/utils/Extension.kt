package com.bulingzhuang.deadline.utils

import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.bulingzhuang.deadline.BuildConfig
import com.bulingzhuang.deadline.R
import com.bulingzhuang.deadline.utils.db.DBUtil

/**
 * Created by bulingzhuang
 * on 2017/8/24
 * E-mail:bulingzhuang@foxmail.com
 */


/**
 * 普通Toast
 */
fun Context.showToast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, length).show()
}

/**
 * 黑底白字的SnakeBar
 */
fun Context.showSnakeBar(msg: String, genView: View, duration: Int = Snackbar.LENGTH_SHORT) {
    val snackBar = Snackbar.make(genView, msg, duration)
    val layout = snackBar.view
    layout.background = ContextCompat.getDrawable(this, R.drawable.snackbar_bg_dark)
    val tv = layout.findViewById<TextView>(android.support.design.R.id.snackbar_text)
    tv.setTextColor(ContextCompat.getColor(this, android.R.color.white))
    snackBar.show()
}

/**
 * 黑底白字的SnakeBar带点击操作
 */
fun Context.showSnakeBarWithAction(msg: String, genView: View, actionTitle: String, actionListener: View.OnClickListener, duration: Int = Snackbar.LENGTH_LONG) {
    val snackBar = Snackbar.make(genView, msg, duration)
    snackBar.setAction(actionTitle, actionListener)
    val layout = snackBar.view
    layout.background = ContextCompat.getDrawable(this, R.drawable.snackbar_bg_dark)
    val tvText = layout.findViewById<TextView>(android.support.design.R.id.snackbar_text)
    val tvAction = layout.findViewById<TextView>(android.support.design.R.id.snackbar_action)
    tvText.setTextColor(ContextCompat.getColor(this, android.R.color.white))
    tvAction.setTextColor(ContextCompat.getColor(this, R.color.amber600))
    snackBar.show()
}

fun showLogE(msg: String, tag: String = "BLZ") {
    if (BuildConfig.DEBUG) {
        Log.e(tag, msg)
    }
}

val Context.database: DBUtil get() = DBUtil.getInstance(this.applicationContext)