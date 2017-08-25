package com.bulingzhuang.deadline.impl.presenters

import android.content.Context
import com.bulingzhuang.deadline.bean.WeatherModel
import com.bulingzhuang.deadline.impl.interactors.MainInteractorImpl
import com.bulingzhuang.deadline.interfaces.presenters.MainPresenter
import com.bulingzhuang.deadline.interfaces.views.MainView
import com.bulingzhuang.deadline.utils.Constants
import com.bulingzhuang.deadline.utils.SharePreferencesUtil
import com.bulingzhuang.deadline.utils.net.ApiCallback
import com.bulingzhuang.deadline.utils.showLogE
import com.google.gson.Gson

/**
 * Created by bulingzhuang
 * on 2017/8/24
 * E-mail:bulingzhuang@foxmail.com
 */
class MainPresenterImpl(view: MainView) : MainPresenter {

    private var mMainView: MainView = view
    private var mMainInteractor: MainInteractorImpl = MainInteractorImpl()

    override fun getWeatherData(context: Context) {
        val lastRefreshDataStr = SharePreferencesUtil.getString(Constants.MAIN_LAST_WEATHER_REFRESH_DATA)
        showLogE("上次一访问天气接口数据：$lastRefreshDataStr")
        var refresh = true
        val gson = Gson()
        if (lastRefreshDataStr.isNotEmpty()) {
            val data = gson.fromJson(lastRefreshDataStr, WeatherModel.ResultsBean.NowBean::class.java)
            if (System.currentTimeMillis() - data.createTime < 1000 * 60 * 30) {
                showLogE("使用本地缓存的数据")
                refresh = false
                mMainView.updateWeather(data)
            }
        }
        if (refresh) {
            mMainInteractor.getWeatherData(object : ApiCallback<WeatherModel>() {
                override fun onFailure(msg: String?) {
                    mMainView.updateError(msg)
                }

                override fun onFinish() {

                }

                override fun onSuccess(module: WeatherModel) {
                    val data = module.results[0].now
                    mMainView.updateWeather(data)
                    data.createTime = System.currentTimeMillis()
                    val jsonData = gson.toJson(data)
                    showLogE("保存了天气数据：$jsonData")
                    SharePreferencesUtil.setValue(context, Constants.MAIN_LAST_WEATHER_REFRESH_DATA, jsonData)
                }

            })
        }
    }

    override fun doBeforeDestroy() {
        mMainInteractor.doBeforeDestroy()
    }

}