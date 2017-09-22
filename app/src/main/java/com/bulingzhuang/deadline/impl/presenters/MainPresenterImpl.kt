package com.bulingzhuang.deadline.impl.presenters

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.bulingzhuang.deadline.R
import com.bulingzhuang.deadline.base.BaseActivity
import com.bulingzhuang.deadline.bean.DeadlineModel
import com.bulingzhuang.deadline.bean.WeatherModel
import com.bulingzhuang.deadline.impl.interactors.MainInteractorImpl
import com.bulingzhuang.deadline.interfaces.presenters.MainPresenter
import com.bulingzhuang.deadline.interfaces.views.MainView
import com.bulingzhuang.deadline.utils.*
import com.bulingzhuang.deadline.utils.db.DBUtil
import com.bulingzhuang.deadline.utils.db.DeadlineRowParser
import com.bulingzhuang.deadline.utils.net.ApiCallback
import com.bulingzhuang.deadline.views.adapters.DeadlineModelAdapter
import com.bulingzhuang.deadline.views.fragments.ContentDialogFragment
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

    /**
     * 获取天气数据
     */
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
                mMainView.updateWeather(data,false)
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

    enum class DialogType(val typeName: String) {
        ADD("新增"), EDIT("编辑")
    }

    /**
     * 显示新增/编辑对话框
     */
    override fun showDialog(context: AppCompatActivity, data: DeadlineModel?) {
        if (data != null) {
            ContentDialogFragment.newInstance(data).show(context.supportFragmentManager, "contentDialog")
        } else {
            ContentDialogFragment.newInstance().show(context.supportFragmentManager, "contentDialog")
        }
    }

    /**
     * 添加一条数据
     */
    override fun insertItem(context: Context, content: String, typeName: String, startTime: Long, endTime: Long, textColor: String, startColor: String, endColor: String, isGradient: Boolean) {
        showLogE("内容=$content，类型=$typeName，开始时间=$startTime，结束时间=$endTime，文字颜色=$textColor，开始颜色=$startColor，结束颜色=$endColor，使用渐变=$isGradient")
        val isGradientStr = if (isGradient) {
            "true"
        } else {
            "false"
        }
        context.database.use {
            //添加数据
            insert(DBUtil.TABLE_NAME_deadline,
                    DBUtil.DEADLINE_content to content,
                    DBUtil.DEADLINE_type to typeName,
                    DBUtil.DEADLINE_startTime to startTime,
                    DBUtil.DEADLINE_endTime to endTime,
                    DBUtil.DEADLINE_startColor to startColor,
                    DBUtil.DEADLINE_endColor to endColor,
                    DBUtil.DEADLINE_textColor to textColor,
                    DBUtil.DEADLINE_isGradient to isGradientStr)

            val whereArgs = select(DBUtil.TABLE_NAME_deadline).whereArgs("_id > {maxId}", "maxId" to mAdapter.getMaxId())
            val parseList = whereArgs.parseList(DeadlineRowParser())
            showLogE("新增条目数据库查询结果：")
            for (model in parseList) {
                showLogE(model.toString())
            }
            mAdapter.addData(parseList)
        }
    }

    /**
     * 初始化Adapter
     */
    override fun initAdapter(context: Context, recyclerView: RecyclerView) {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        mAdapter = DeadlineModelAdapter(context,System.currentTimeMillis())
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

    /**
     * 检查Adapter是否需要刷新
     */
    override fun checkAdapter() {
        mAdapter.checkAdapter()
    }

    /**
     * 销毁页面操作
     */
    override fun doBeforeDestroy() {
        mMainInteractor.doBeforeDestroy()
    }

}