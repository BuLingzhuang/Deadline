package com.bulingzhuang.deadline.views.activitys

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bulingzhuang.deadline.R
import com.bulingzhuang.deadline.bean.DeadlineModel
import com.bulingzhuang.deadline.bean.TypeColorModel
import com.bulingzhuang.deadline.utils.Constants
import com.bulingzhuang.deadline.utils.SharePreferencesUtil
import com.bulingzhuang.deadline.views.fragments.ColorDialogFragment
import com.bulingzhuang.deadline.views.ui.CircleProgressView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    private lateinit var mColorModelList: ArrayList<TypeColorModel.ColorModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        init()
    }

    private fun init() {

        getDefaultColor()

        val set = AnimatorSet()
        val workAnim = ObjectAnimator.ofFloat(cpv_work, "alpha", 0f, 1f).setDuration(1000)
        val festivalAnim = ObjectAnimator.ofFloat(cpv_festival, "alpha", 0f, 1f).setDuration(1000)
        val birthdayAnim = ObjectAnimator.ofFloat(cpv_birthday, "alpha", 0f, 1f).setDuration(1000)
        val familyAnim = ObjectAnimator.ofFloat(cpv_family, "alpha", 0f, 1f).setDuration(1000)
        val otherAnim = ObjectAnimator.ofFloat(cpv_other, "alpha", 0f, 1f).setDuration(1000)

        val workRotation = ObjectAnimator.ofFloat(cpv_work, "rotationY", -180f, 0f).setDuration(1000)
        val festivalRotation = ObjectAnimator.ofFloat(cpv_festival, "rotationY", -180f, 0f).setDuration(1000)
        val birthdayRotation = ObjectAnimator.ofFloat(cpv_birthday, "rotationY", -180f, 0f).setDuration(1000)
        val familyRotation = ObjectAnimator.ofFloat(cpv_family, "rotationY", -180f, 0f).setDuration(1000)
        val otherRotation = ObjectAnimator.ofFloat(cpv_other, "rotationY", -180f, 0f).setDuration(1000)
        val delayDuration = 233L
        workAnim.startDelay = 0L
        workRotation.startDelay = 0L
        festivalAnim.startDelay = delayDuration
        festivalRotation.startDelay = delayDuration
        birthdayAnim.startDelay = delayDuration * 2
        birthdayRotation.startDelay = delayDuration * 2
        familyAnim.startDelay = 0
        familyRotation.startDelay = 0
        otherAnim.startDelay = delayDuration
        otherRotation.startDelay = delayDuration
        set.playTogether(workAnim, festivalAnim, birthdayAnim, familyAnim, otherAnim, workRotation, festivalRotation, birthdayRotation, familyRotation, otherRotation)

        rl_defaultColor.setOnClickListener {
            if (fl_defaultColor.visibility == View.VISIBLE) { //收起操作
                iv_defaultColor.animate().rotationX(0f)
                fl_defaultColor.visibility = View.GONE
                cpv_work.alpha = 0f
                cpv_festival.alpha = 0f
                cpv_birthday.alpha = 0f
                cpv_family.alpha = 0f
                cpv_other.alpha = 0f
            } else { //展示操作
                iv_defaultColor.animate().rotationX(180f)
                fl_defaultColor.visibility = View.VISIBLE
                set.start()
            }
        }

        setCpvListener(cpv_work, cpv_festival, cpv_birthday, cpv_family, cpv_other)
    }

    /**
     * 取默认颜色
     */
    private fun getDefaultColor() {
        val data = SharePreferencesUtil.getString(Constants.DEFAULT_COLOR_DATA)
        if (data.isNotEmpty()) {
            val colorModel = Gson().fromJson(data, TypeColorModel::class.java)
            mColorModelList = colorModel.typeList
        } else {
            mColorModelList = ArrayList()
            mColorModelList.add(TypeColorModel.ColorModel(DeadlineModel.Type.WORK.typeName, true, "#c0ca33", "#00897b"))
            mColorModelList.add(TypeColorModel.ColorModel(DeadlineModel.Type.FESTIVAL.typeName, true, "#00897b", "#4e6cef"))
            mColorModelList.add(TypeColorModel.ColorModel(DeadlineModel.Type.BIRTHDAY.typeName, true, "#4e6cef", "#8e24aa"))
            mColorModelList.add(TypeColorModel.ColorModel(DeadlineModel.Type.FAMILY.typeName, true, "#8e24aa", "#f4511e"))
            mColorModelList.add(TypeColorModel.ColorModel(DeadlineModel.Type.OTHER.typeName, true, "#f4511e", "#fdd835"))
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
    fun setDefaultColor(model: TypeColorModel.ColorModel) {
        when (model.typeName) {
            DeadlineModel.Type.WORK.typeName -> {
                handleColorWithAnim(cpv_work, model)
            }
            DeadlineModel.Type.FESTIVAL.typeName -> {
                handleColorWithAnim(cpv_festival, model)
            }
            DeadlineModel.Type.BIRTHDAY.typeName -> {
                handleColorWithAnim(cpv_birthday, model)
            }
            DeadlineModel.Type.FAMILY.typeName -> {
                handleColorWithAnim(cpv_family, model)
            }
            DeadlineModel.Type.OTHER.typeName -> {
                handleColorWithAnim(cpv_other, model)
            }
        }
    }

    /**
     * 保存修改后的颜色
     */
    private fun handleColorWithAnim(view: CircleProgressView, model: TypeColorModel.ColorModel) {
        mColorModelList.filter { it.typeName == model.typeName }.map {
            it.contentColor = model.contentColor
            it.endColor = model.endColor
            it.isGradient = model.isGradient
        }
        val json = Gson().toJson(TypeColorModel(mColorModelList))
        SharePreferencesUtil.setValue(this,Constants.DEFAULT_COLOR_DATA,json)

        val rotation = ObjectAnimator.ofFloat(view, "rotationY", -180f, 0f).setDuration(1000)
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
