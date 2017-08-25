package com.bulingzhuang.deadline.base

import android.app.Application
import com.bulingzhuang.deadline.utils.SharePreferencesUtil

/**
 * Created by bulingzhuang
 * on 2017/8/25
 * E-mail:bulingzhuang@foxmail.com
 */
class DeadlineApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        //初始化SharePreference
        SharePreferencesUtil.initializeInstance(this)
    }
}