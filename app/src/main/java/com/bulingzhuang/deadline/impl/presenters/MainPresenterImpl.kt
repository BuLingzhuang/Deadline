package com.bulingzhuang.deadline.impl.presenters

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.bulingzhuang.deadline.bean.WeatherModel
import com.bulingzhuang.deadline.impl.interactors.MainInteractorImpl
import com.bulingzhuang.deadline.interfaces.presenters.MainPresenter
import com.bulingzhuang.deadline.interfaces.views.MainView
import com.bulingzhuang.deadline.utils.Constants
import com.bulingzhuang.deadline.utils.SharePreferencesUtil
import com.bulingzhuang.deadline.utils.database
import com.bulingzhuang.deadline.utils.db.DBUtil
import com.bulingzhuang.deadline.utils.db.DeadlineRowParser
import com.bulingzhuang.deadline.utils.net.ApiCallback
import com.bulingzhuang.deadline.utils.showLogE
import com.bulingzhuang.deadline.views.adapters.DeadlineModelAdapter
import com.google.gson.Gson
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select

/**
 * Created by bulingzhuang
 * on 2017/8/24
 * E-mail:bulingzhuang@foxmail.com
 */
class MainPresenterImpl(view: MainView) : MainPresenter {

    private var mMainView: MainView = view
    private var mMainInteractor: MainInteractorImpl = MainInteractorImpl()

    private lateinit var mAdapter: DeadlineModelAdapter

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

    override fun insertItem(context: Context,str: String) {
        context.database.use {
            //添加数据
            val currentTimeMillis = System.currentTimeMillis()
            val split = str.split(",")
            val num = split[0].toInt()
            val num1 = split[1].toInt()
            insert(DBUtil.TABLE_NAME_deadline,
                    DBUtil.DEADLINE_content to str,
                    DBUtil.DEADLINE_type to "节日",
                    DBUtil.DEADLINE_startTime to (currentTimeMillis - num*3600*1000),
                    DBUtil.DEADLINE_endTime to (currentTimeMillis + num1*3600*1000),
                    DBUtil.DEADLINE_startColor to "#FFFFFF",
                    DBUtil.DEADLINE_endColor to "#000FFF")

            val whereArgs = select(DBUtil.TABLE_NAME_deadline).whereArgs("_id > {maxId}", "maxId" to mAdapter.getMaxId())
            val parseList = whereArgs.parseList(DeadlineRowParser())
            showLogE("新增条目数据库查询结果：")
            for (model in parseList) {
                showLogE(model.toString())
            }
            mAdapter.addData(parseList)
        }
    }

    override fun initAdapter(context: Context, recyclerView: RecyclerView) {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        mAdapter = DeadlineModelAdapter(context, System.currentTimeMillis())
        recyclerView.adapter = mAdapter
        context.database.use {
            val select = select(DBUtil.TABLE_NAME_deadline)
            val parseList = select.parseList(DeadlineRowParser())
            showLogE("查询数据库结果：")
            for (model in parseList) {
                showLogE(model.toString())
            }
            mAdapter.refreshData(parseList)
        }
    }

    override fun doBeforeDestroy() {
        mMainInteractor.doBeforeDestroy()
    }

}