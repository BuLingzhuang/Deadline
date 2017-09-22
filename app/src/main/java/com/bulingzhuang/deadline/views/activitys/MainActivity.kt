package com.bulingzhuang.deadline.views.activitys

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.bulingzhuang.deadline.R
import com.bulingzhuang.deadline.bean.WeatherModel
import com.bulingzhuang.deadline.impl.presenters.MainPresenterImpl
import com.bulingzhuang.deadline.interfaces.presenters.MainPresenter
import com.bulingzhuang.deadline.interfaces.views.MainView
import com.bulingzhuang.deadline.utils.showSnakeBar

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), MainView {

    lateinit var mPresenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        init()
//        btn_test.setOnClickListener {
//            database.use {
//                //添加数据
//                val currentTimeMillis = System.currentTimeMillis()
//                insert(DBUtil.TABLE_NAME_deadline,
//                        DBUtil.DEADLINE_content to et_content.text.toString(),
//                        DBUtil.DEADLINE_type to "节日",
//                        DBUtil.DEADLINE_startTime to currentTimeMillis,
//                        DBUtil.DEADLINE_endTime to (currentTimeMillis - 30000),
//                        DBUtil.DEADLINE_startColor to "#FFFFFF",
//                        DBUtil.DEADLINE_endColor to "#000FFF")
//            }
//        }
//        btn1.setOnClickListener {
//            database.use {
//                //删除数据
//                delete(DBUtil.TABLE_NAME_deadline,
//                        "(${DBUtil.DEADLINE_id} = {${DBUtil.DEADLINE_id}})",
//                        DBUtil.DEADLINE_id to et.text.toString())
//            }
//        }
//        btn.setOnClickListener {
//            val split = et.text.toString()
//            mPresenter.insertItem(this,split)
//        }
    }

    private fun init() {
        mPresenter = MainPresenterImpl(this)
        mPresenter.initAdapter(this, rv_content)
    }

    override fun onResume() {
        super.onResume()
        mPresenter.getWeatherData(this)
        mPresenter.checkAdapter()
    }

    override fun onDestroy() {
        mPresenter.doBeforeDestroy()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {//新增
                mPresenter.showDialog(this,null)
                return true
            }
            R.id.action_accurateUpdate -> {//精确更新
                //TODO 根据对勾使用系统默认的30分钟更新，或者开启服务，一分钟刷新一次(稍微耗电)
                return true
            }
            R.id.action_feelingLucky -> {//试试手气
                //TODO 随机变换颜色
                return true
            }
            R.id.action_about -> {//关于
                //TODO 关于
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * 更新天气信息
     */
    override fun updateWeather(data: WeatherModel.ResultsBean.NowBean, showAnim:Boolean) {
        val temp = data.temperature
        val code = data.code
        if (temp.startsWith("-")) {
            temp_weather.refreshTempData(temp.substring(1, temp.length).toInt(), showAnim, true)
        } else {
            temp_weather.refreshTempData(temp.toInt(), showAnim)
        }
        when (code) {
            "4", "5", "6", "7", "8", "9", "30", "31", "34", "35", "36" -> {
                iv_weather.setImageResource(R.drawable.ic_cloud)
            }
            "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25" -> {
                iv_weather.setImageResource(R.drawable.ic_rain)
            }
            else -> {
                iv_weather.setImageResource(R.drawable.ic_sun)
            }
        }
    }

    override fun updateError(eText: String?) {
        showSnakeBar(eText?.let { it } ?: "请求失败", cl_gen)
    }
}
