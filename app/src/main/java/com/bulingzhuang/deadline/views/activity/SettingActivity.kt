package com.bulingzhuang.deadline.views.activity

import android.animation.ObjectAnimator
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
import com.bulingzhuang.deadline.utils.Tools
import com.bulingzhuang.deadline.views.fragment.ColorDialogFragment
import com.bulingzhuang.deadline.views.ui.CircleProgressView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_setting.*
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import com.bulingzhuang.deadline.utils.showLogE


class SettingActivity : AppCompatActivity() {

    private lateinit var mColorModelList: ArrayList<TypeColorModel.ColorModel>

    private lateinit var mPresenter: SettingPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        init()
    }

    private fun init() {

        mPresenter = SettingPresenterImpl()

        rl_back.setOnClickListener {
            if (rl_search_gen.visibility == View.VISIBLE) {
                showLogE("执行了")
                hideSearchFunction()
            }else{
                finish()
            }
        }

        getDefaultColor()

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
        btn_search.setOnClickListener {
            hideSearchFunction()
        }

        cpv_replace.setOnClickListener {
            var delayPosition = 0
            mColorModelList.forEach {
                when (it.typeName) {
                    DeadlineModel.Type.WORK.typeName -> {
                        val colorModel = TypeColorModel.ColorModel(DeadlineModel.Type.WORK.typeName, true, "#00897b", "#c0ca33")
                        if (!Tools.compareColor(it, colorModel)) {
                            setDefaultColor(colorModel, ++delayPosition, -360f)
                        }
                    }
                    DeadlineModel.Type.FESTIVAL.typeName -> {
                        val colorModel = TypeColorModel.ColorModel(DeadlineModel.Type.FESTIVAL.typeName, true, "#4e6cef", "#00897b")
                        if (!Tools.compareColor(it, colorModel)) {
                            setDefaultColor(colorModel, ++delayPosition, -360f)
                        }
                    }
                    DeadlineModel.Type.BIRTHDAY.typeName -> {
                        val colorModel = TypeColorModel.ColorModel(DeadlineModel.Type.BIRTHDAY.typeName, true, "#8e24aa", "#4e6cef")
                        if (!Tools.compareColor(it, colorModel)) {
                            setDefaultColor(colorModel, ++delayPosition, -360f)
                        }
                    }
                    DeadlineModel.Type.FAMILY.typeName -> {
                        val colorModel = TypeColorModel.ColorModel(DeadlineModel.Type.FAMILY.typeName, true, "#f4511e", "#8e24aa")
                        if (!Tools.compareColor(it, colorModel)) {
                            setDefaultColor(colorModel, ++delayPosition, -360f)
                        }
                    }
                    DeadlineModel.Type.OTHER.typeName -> {
                        val colorModel = TypeColorModel.ColorModel(DeadlineModel.Type.OTHER.typeName, true, "#ffb300", "#dd191d")
                        if (!Tools.compareColor(it, colorModel)) {
                            setDefaultColor(colorModel, ++delayPosition, -360f)
                        }
                    }
                }
            }
        }

        setCpvListener(cpv_work, cpv_festival, cpv_birthday, cpv_family, cpv_other)
    }

    /**
     * 隐藏查询功能
     */
    private fun hideSearchFunction() {
        //隐藏软键盘
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(et_search.windowToken, 0)
        tv_title.text = resources.getString(R.string.action_settings)
        TransitionManager.beginDelayedTransition(ll_gen, Fade())
        rl_search_gen.visibility = View.GONE
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        showLogE("有操作，action=${event?.action}，keyCode=$keyCode")
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            showLogE("到第一层")
            if (rl_search_gen.visibility == View.VISIBLE) {
                showLogE("执行了")
                hideSearchFunction()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 取默认颜色
     */
    private fun getDefaultColor() {
        val data = SharePreferencesUtil.getString(Constants.SP_DEFAULT_COLOR_DATA)
        if (data.isNotEmpty()) {
            val colorModel = Gson().fromJson(data, TypeColorModel::class.java)
            mColorModelList = colorModel.typeList
        } else {
            mColorModelList = ArrayList()
            mColorModelList.add(TypeColorModel.ColorModel(DeadlineModel.Type.WORK.typeName, true, "#00897b", "#c0ca33"))
            mColorModelList.add(TypeColorModel.ColorModel(DeadlineModel.Type.FESTIVAL.typeName, true, "#4e6cef", "#00897b"))
            mColorModelList.add(TypeColorModel.ColorModel(DeadlineModel.Type.BIRTHDAY.typeName, true, "#8e24aa", "#4e6cef"))
            mColorModelList.add(TypeColorModel.ColorModel(DeadlineModel.Type.FAMILY.typeName, true, "#f4511e", "#8e24aa"))
            mColorModelList.add(TypeColorModel.ColorModel(DeadlineModel.Type.OTHER.typeName, true, "#ffb300", "#dd191d"))
        }

        mColorModelList.forEach {
            when (it.typeName) {
                DeadlineModel.Type.WORK.typeName -> {
                    handleColor(cpv_work, it)
                }
                DeadlineModel.Type.FESTIVAL.typeName -> {
                    handleColor(cpv_festival, it)
                }
                DeadlineModel.Type.BIRTHDAY.typeName -> {
                    handleColor(cpv_birthday, it)
                }
                DeadlineModel.Type.FAMILY.typeName -> {
                    handleColor(cpv_family, it)
                }
                DeadlineModel.Type.OTHER.typeName -> {
                    handleColor(cpv_other, it)
                }
            }
        }
    }

    /**
     * 把颜色设置到布局上
     */
    private fun handleColor(view: CircleProgressView, model: TypeColorModel.ColorModel) {
        if (model.isGradient) {
            view.setColor(model.contentColor, model.endColor)
        } else {
            view.setColor(model.contentColor)
        }
    }

    /**
     * 修改默认颜色
     */
    fun setDefaultColor(model: TypeColorModel.ColorModel, position: Int, rotateValue: Float = -180f) {
        when (model.typeName) {
            DeadlineModel.Type.WORK.typeName -> {
                handleColorWithAnim(cpv_work, model, position, rotateValue)
            }
            DeadlineModel.Type.FESTIVAL.typeName -> {
                handleColorWithAnim(cpv_festival, model, position, rotateValue)
            }
            DeadlineModel.Type.BIRTHDAY.typeName -> {
                handleColorWithAnim(cpv_birthday, model, position, rotateValue)
            }
            DeadlineModel.Type.FAMILY.typeName -> {
                handleColorWithAnim(cpv_family, model, position, rotateValue)
            }
            DeadlineModel.Type.OTHER.typeName -> {
                handleColorWithAnim(cpv_other, model, position, rotateValue)
            }
        }
    }

    /**
     * 保存修改后的颜色
     */
    private fun handleColorWithAnim(view: CircleProgressView, model: TypeColorModel.ColorModel, position: Int, rotateValue: Float = -180f) {
        mColorModelList.filter { it.typeName == model.typeName }.map {
            it.contentColor = model.contentColor
            it.endColor = model.endColor
            it.isGradient = model.isGradient
        }
        val json = Gson().toJson(TypeColorModel(mColorModelList))
        SharePreferencesUtil.setValue(this, Constants.SP_DEFAULT_COLOR_DATA, json)

        val rotation = ObjectAnimator.ofFloat(view, "rotationY", rotateValue, 0f).setDuration(1000)
        rotation.startDelay = position * 233L
        rotation.addUpdateListener {
            val fl = it.animatedValue as Float
            if (Math.abs(fl) < 90) {
                if (model.isGradient) {
                    view.setColor(model.contentColor, model.endColor)
                } else {
                    view.setColor(model.contentColor)
                }
            }
        }
        rotation.start()
    }

    /**
     * 设置默认颜色的点击监听
     */
    private fun setCpvListener(vararg views: View) {
        val cpvListener = View.OnClickListener { view ->
            intent2ColorDialog(view.id)
        }

        views.forEach { it.setOnClickListener(cpvListener) }
    }

    private fun intent2ColorDialog(id: Int) {
        var data: TypeColorModel.ColorModel? = null
        when (id) {
            R.id.cpv_work -> {
                mColorModelList.filter { it.typeName == DeadlineModel.Type.WORK.typeName }.forEach { data = it }
            }
            R.id.cpv_festival -> {
                mColorModelList.filter { it.typeName == DeadlineModel.Type.FESTIVAL.typeName }.forEach { data = it }
            }
            R.id.cpv_birthday -> {
                mColorModelList.filter { it.typeName == DeadlineModel.Type.BIRTHDAY.typeName }.forEach { data = it }
            }
            R.id.cpv_family -> {
                mColorModelList.filter { it.typeName == DeadlineModel.Type.FAMILY.typeName }.forEach { data = it }
            }
            R.id.cpv_other -> {
                mColorModelList.filter { it.typeName == DeadlineModel.Type.OTHER.typeName }.forEach { data = it }
            }
        }
        if (data != null) {
            ColorDialogFragment.newInstance(data!!).show(supportFragmentManager, "colorDialog")
        }
    }
}
