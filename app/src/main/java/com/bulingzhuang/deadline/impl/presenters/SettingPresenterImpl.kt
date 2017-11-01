package com.bulingzhuang.deadline.impl.presenters

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.bulingzhuang.deadline.interfaces.presenters.SettingPresenter
import com.bulingzhuang.deadline.utils.*
import com.bulingzhuang.deadline.views.ui.CircleProgressView
import com.bulingzhuang.deadline.views.ui.GrayQuickTipsView

/**
 * Created by bulingzhuang
 * on 2017/10/25
 * E-mail:bulingzhuang@foxmail.com
 */
class SettingPresenterImpl : SettingPresenter {

    //历史城市list
    private lateinit var mHistoryCityList: ArrayList<String>
    //检索城市list
    private lateinit var mSearchCityList: ArrayList<String>
    //所有城市字符串
    private lateinit var mAllCityStr: String
    //历史城市字符串
    private lateinit var mHistoryCityStr:String

    /**
     * 初始化选择城市Adapter
     */
    override fun initDefaultCityAdapter(context: Context, view: GrayQuickTipsView, etView: EditText) {
        mAllCityStr = Tools.readAssets(context, "area.txt")
        mHistoryCityStr = SharePreferencesUtil.getString(Constants.SP_HISTORY_WEATHER_CITY)
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
                val sb = StringBuilder()
                mHistoryCityList.add(str)
                mHistoryCityList.forEach { sb.append(it).append(",") }
                showLogE("存入历史城市：$sb")
                var saveStr = sb.toString()
                if (saveStr.endsWith(",")) {
                    saveStr = saveStr.substring(0,saveStr.length-1)
                }
                SharePreferencesUtil.setValue(context, Constants.SP_HISTORY_WEATHER_CITY, saveStr)
                context.showToast(str)
            }

        })
    }

    private fun searchCity(parameter: String) {
        mSearchCityList.clear()
        if (parameter.isNotEmpty()) {
            var index = 0
            while (index != -1) {
                ++index
                index = mAllCityStr.indexOf(parameter, index)
                if (index!=-1){
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
}