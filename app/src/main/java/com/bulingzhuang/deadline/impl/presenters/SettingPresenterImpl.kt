package com.bulingzhuang.deadline.impl.presenters

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.bulingzhuang.deadline.R
import com.bulingzhuang.deadline.bean.DeadlineModel
import com.bulingzhuang.deadline.bean.TypeColorModel
import com.bulingzhuang.deadline.interfaces.presenters.SettingPresenter
import com.bulingzhuang.deadline.interfaces.views.SettingView
import com.bulingzhuang.deadline.utils.*
import com.bulingzhuang.deadline.views.fragment.ColorDialogFragment
import com.bulingzhuang.deadline.views.ui.CircleProgressView
import com.bulingzhuang.deadline.views.ui.GrayQuickTipsView
import com.google.gson.Gson

/**
 * Created by bulingzhuang
 * on 2017/10/25
 * E-mail:bulingzhuang@foxmail.com
 */
class SettingPresenterImpl(view: SettingView) : SettingPresenter {

    private var mSettingView: SettingView = view

    //默认颜色model
    private lateinit var mColorModelList: ArrayList<TypeColorModel.ColorModel>

    //历史城市list
    private lateinit var mHistoryCityList: ArrayList<String>
    //检索城市list
    private lateinit var mSearchCityList: ArrayList<String>
    //所有城市字符串
    private lateinit var mAllCityStr: String
    //历史城市字符串
    private lateinit var mHistoryCityStr: String

    /**
     * 初始化选择城市Adapter
     */
    override fun initDefaultCityAdapter(context: Context, view: GrayQuickTipsView, etView: EditText) {
        mAllCityStr = Tools.readAssets(context, "area.txt")
        mHistoryCityStr = SharePreferencesUtil.getString(Constants.SP_HISTORY_WEATHER_CITY_LIST)
        mHistoryCityList = ArrayList()
        mSearchCityList = ArrayList()
        if (mHistoryCityStr.isNotEmpty()) {
            mHistoryCityList.addAll(mHistoryCityStr.split(","))
        }
        view.setData(mHistoryCityList, null)
        etView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchCity(s.toString())
                view.setData(mHistoryCityList, mSearchCityList)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        view.setOnViewClickListener(object : GrayQuickTipsView.OnViewClickListener {
            override fun onClick(str: String) {
                changeWeatherCity(str, context)
            }
        })
    }

    /**
     * 设置默认颜色的点击监听
     */
    override fun setCpvListener(activity: AppCompatActivity, vararg views: View) {
        val cpvListener = View.OnClickListener { view ->
            val data = getColorModel(view.id)
            if (data != null) {
                ColorDialogFragment.newInstance(data).show(activity.supportFragmentManager, "colorDialog")
            }
        }

        views.forEach { it.setOnClickListener(cpvListener) }
    }

    /**
     * 初始化默认颜色显隐动画
     */
    override fun initAnimSet(vararg views: CircleProgressView): AnimatorSet {
        val set = AnimatorSet()
        val delayDuration = 233L
        val animList = ArrayList<Animator>()
        for (position in views.indices) {
            val itemView = views[position]
            val alphaAnim = ObjectAnimator.ofFloat(itemView, "alpha", 0f, 1f).setDuration(1000)
            val rotationYAnim = ObjectAnimator.ofFloat(itemView, "rotationY", -180f, 0f).setDuration(1000)
            alphaAnim.startDelay = position % 3 * delayDuration
            rotationYAnim.startDelay = position % 3 * delayDuration
            animList.add(alphaAnim)
            animList.add(rotationYAnim)
        }
        set.playTogether(animList)
        return set
    }

    /**
     * 保存修改后的颜色
     */
    override fun handleColorWithAnim(context: Context,view: CircleProgressView, model: TypeColorModel.ColorModel, position: Int, rotateValue: Float) {
        mColorModelList.filter { it.typeName == model.typeName }.map {
            it.contentColor = model.contentColor
            it.endColor = model.endColor
            it.isGradient = model.isGradient
        }
        val json = Gson().toJson(TypeColorModel(mColorModelList))
        SharePreferencesUtil.setValue(context, Constants.SP_DEFAULT_COLOR_DATA, json)

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
     * 取默认颜色
     */
    override fun getDefaultColor(workView:CircleProgressView,festivalView:CircleProgressView,birthdayView:CircleProgressView,familyView:CircleProgressView,otherView:CircleProgressView) {
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
                    handleColor(workView, it)
                }
                DeadlineModel.Type.FESTIVAL.typeName -> {
                    handleColor(festivalView, it)
                }
                DeadlineModel.Type.BIRTHDAY.typeName -> {
                    handleColor(birthdayView, it)
                }
                DeadlineModel.Type.FAMILY.typeName -> {
                    handleColor(familyView, it)
                }
                DeadlineModel.Type.OTHER.typeName -> {
                    handleColor(otherView, it)
                }
            }
        }
    }


    /**
     * 重置ColorModel
     */
    override fun replaceAllColorModel() {
        var delayPosition = 0
        mColorModelList.forEach {
            when (it.typeName) {
                DeadlineModel.Type.WORK.typeName -> {
                    val colorModel = TypeColorModel.ColorModel(DeadlineModel.Type.WORK.typeName, true, "#00897b", "#c0ca33")
                    if (!Tools.compareColor(it, colorModel)) {
                        mSettingView.setDefaultColor(colorModel, ++delayPosition, -360f)
                    }
                }
                DeadlineModel.Type.FESTIVAL.typeName -> {
                    val colorModel = TypeColorModel.ColorModel(DeadlineModel.Type.FESTIVAL.typeName, true, "#4e6cef", "#00897b")
                    if (!Tools.compareColor(it, colorModel)) {
                        mSettingView.setDefaultColor(colorModel, ++delayPosition, -360f)
                    }
                }
                DeadlineModel.Type.BIRTHDAY.typeName -> {
                    val colorModel = TypeColorModel.ColorModel(DeadlineModel.Type.BIRTHDAY.typeName, true, "#8e24aa", "#4e6cef")
                    if (!Tools.compareColor(it, colorModel)) {
                        mSettingView.setDefaultColor(colorModel, ++delayPosition, -360f)
                    }
                }
                DeadlineModel.Type.FAMILY.typeName -> {
                    val colorModel = TypeColorModel.ColorModel(DeadlineModel.Type.FAMILY.typeName, true, "#f4511e", "#8e24aa")
                    if (!Tools.compareColor(it, colorModel)) {
                        mSettingView.setDefaultColor(colorModel, ++delayPosition, -360f)
                    }
                }
                DeadlineModel.Type.OTHER.typeName -> {
                    val colorModel = TypeColorModel.ColorModel(DeadlineModel.Type.OTHER.typeName, true, "#ffb300", "#dd191d")
                    if (!Tools.compareColor(it, colorModel)) {
                        mSettingView.setDefaultColor(colorModel, ++delayPosition, -360f)
                    }
                }
            }
        }
    }

    /**
     *
     * private 抽取方法
     *
     */

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
     * 变更默认天气城市
     */
    private fun changeWeatherCity(str: String, context: Context) {
        var contain = false
        val sb = StringBuilder()
        mHistoryCityList.forEach {
            sb.append(it).append(",")
            if (it == str) {
                contain = true
            }
        }
        if (!contain) {
            mHistoryCityList.add(str)
            sb.append(str)
        }
        showLogE("存入历史城市：$sb")
        var saveStr = sb.toString()
        if (saveStr.endsWith(",")) {
            saveStr = saveStr.substring(0, saveStr.length - 1)
        }
        SharePreferencesUtil.setValue(context, Constants.SP_HISTORY_WEATHER_CITY_LIST, saveStr)
        mSettingView.hideSearchFunction(str)
    }

    /**
     * 本地搜索城市
     */
    private fun searchCity(parameter: String) {
        mSearchCityList.clear()
        if (parameter.isNotEmpty()) {
            var index = 0
            while (index != -1) {
                ++index
                index = mAllCityStr.indexOf(parameter, index)
                if (index != -1) {
                    val startIndex = mAllCityStr.lastIndexOf("\"", index) + 1
                    val endIndex = mAllCityStr.indexOf("\"", index)
                    val result = mAllCityStr.substring(startIndex, endIndex).trim()
                    showLogE("index = $index，startIndex = $startIndex，endIndex = $endIndex，搜索结果：$result")
                    if (result.isNotEmpty() && !mHistoryCityStr.contains(result)) {
                        mSearchCityList.add(result)
                    }
                }
            }
        }
    }

    /**
     * 获取类型对应的ColorModel
     */
    private fun getColorModel(id: Int) :TypeColorModel.ColorModel?{
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
        return data
    }
}