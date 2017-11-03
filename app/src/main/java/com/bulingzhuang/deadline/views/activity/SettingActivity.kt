package com.bulingzhuang.deadline.views.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.view.View
import android.view.WindowManager
import com.bulingzhuang.deadline.R
import com.bulingzhuang.deadline.bean.DeadlineModel
import com.bulingzhuang.deadline.bean.TypeColorModel
import com.bulingzhuang.deadline.impl.presenters.SettingPresenterImpl
import com.bulingzhuang.deadline.interfaces.presenters.SettingPresenter
import com.bulingzhuang.deadline.utils.Constants
import com.bulingzhuang.deadline.utils.SharePreferencesUtil
import kotlinx.android.synthetic.main.activity_setting.*
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import com.bulingzhuang.deadline.interfaces.views.SettingView
import com.bulingzhuang.deadline.utils.showLogE

class SettingActivity : AppCompatActivity(), SettingView {

    private lateinit var mPresenter: SettingPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        init()
    }

    private fun init() {
        mPresenter = SettingPresenterImpl(this)
        mPresenter.initDefaultCityAdapter(this, gqt, et_search)

        val weatherCity = SharePreferencesUtil.getString(Constants.SP_WEATHER_CITY)
        if (weatherCity.isNotEmpty()) {
            tv_defaultCity.text = weatherCity
        }

        rl_back.setOnClickListener {
            if (rl_search_gen.visibility == View.VISIBLE) {
                hideSearchFunction()
            } else {
                finish()
            }
        }

        mPresenter.getDefaultColor(cpv_work, cpv_festival, cpv_birthday, cpv_family, cpv_other)

        val animSet = mPresenter.initAnimSet(cpv_work, cpv_festival, cpv_birthday, cpv_family, cpv_other, cpv_replace)
        rl_defaultColor.setOnClickListener {
            if (fl_defaultColor.visibility == View.VISIBLE) { //收起操作
                iv_defaultColor.animate().rotationX(0f)
                fl_defaultColor.visibility = View.GONE
                cpv_work.alpha = 0f
                cpv_festival.alpha = 0f
                cpv_birthday.alpha = 0f
                cpv_family.alpha = 0f
                cpv_other.alpha = 0f
                cpv_replace.alpha = 0f
            } else { //展示操作
                iv_defaultColor.animate().rotationX(180f)
                fl_defaultColor.visibility = View.VISIBLE
                animSet.start()
            }
        }

        tv_defaultCity.setOnClickListener {
            showSearchFunction()
        }
        btn_search.setOnClickListener {
            hideSearchFunction()
        }

        cpv_replace.setOnClickListener {
            mPresenter.replaceAllColorModel()
        }

        mPresenter.setCpvListener(this, cpv_work, cpv_festival, cpv_birthday, cpv_family, cpv_other)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (rl_search_gen.visibility == View.VISIBLE) {
                hideSearchFunction()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 展示查询功能
     */
    fun showSearchFunction() {
        tv_title.text = String.format("%s - %s", resources.getString(R.string.action_settings), resources.getString(R.string.setting_default_city))
        TransitionManager.beginDelayedTransition(ll_gen, Fade())
        rl_search_gen.visibility = View.VISIBLE
        et_search.isFocusable = true
        et_search.isFocusableInTouchMode = true
        et_search.requestFocus()
        et_search.requestFocusFromTouch()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        //显示软键盘
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    /**
     * 隐藏查询功能
     */
    override fun hideSearchFunction(city: String) {
        if (city.isNotEmpty()) {
            tv_defaultCity.text = city
            SharePreferencesUtil.setValue(this, Constants.SP_WEATHER_CITY, city)
        }
        //隐藏软键盘
        et_search.setText("")
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(et_search.windowToken, 0)
        tv_title.text = resources.getString(R.string.action_settings)
        TransitionManager.beginDelayedTransition(ll_gen, Fade())
        rl_search_gen.visibility = View.GONE
    }

    /**
     * 修改默认颜色
     */
    override fun setDefaultColor(model: TypeColorModel.ColorModel, position: Int, rotateValue: Float) {
        when (model.typeName) {
            DeadlineModel.Type.WORK.typeName -> {
                mPresenter.handleColorWithAnim(this, cpv_work, model, position, rotateValue)
            }
            DeadlineModel.Type.FESTIVAL.typeName -> {
                mPresenter.handleColorWithAnim(this, cpv_festival, model, position, rotateValue)
            }
            DeadlineModel.Type.BIRTHDAY.typeName -> {
                mPresenter.handleColorWithAnim(this, cpv_birthday, model, position, rotateValue)
            }
            DeadlineModel.Type.FAMILY.typeName -> {
                mPresenter.handleColorWithAnim(this, cpv_family, model, position, rotateValue)
            }
            DeadlineModel.Type.OTHER.typeName -> {
                mPresenter.handleColorWithAnim(this, cpv_other, model, position, rotateValue)
            }
        }
    }

}
