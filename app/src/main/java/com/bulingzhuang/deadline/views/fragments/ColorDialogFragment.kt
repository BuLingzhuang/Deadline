package com.bulingzhuang.deadline.views.fragments

import android.animation.Animator
import android.app.Dialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import com.bulingzhuang.deadline.R
import com.bulingzhuang.deadline.views.ui.ColorRadioGroup

/**
 * Created by bulingzhuang
 * on 2017/9/15
 * E-mail:bulingzhuang@foxmail.com
 */
class ColorDialogFragment : DialogFragment() {

    companion object {
        val Args_contentColor = "Args_contentColor"
        val Args_endColor = "endColor"
        val Args_isGradient = "isGradient"
        fun newInstance(isGradient: Boolean, contentColor: String = "#37474f", endColor: String = "#37474f"): ColorDialogFragment {
            val args = Bundle()
            args.putString(Args_contentColor, contentColor)
            args.putString(Args_endColor, endColor)
            args.putBoolean(Args_isGradient, isGradient)
            val fragment = ColorDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    //表示当前正在录入颜色的字段
    enum class InputType {
        Content, End
    }

    lateinit var tvContentColor:TextView
    lateinit var llContentColor: LinearLayout
    lateinit var llEndColor: LinearLayout
    lateinit var vContentColor: View
    lateinit var vEndColor: View
    lateinit var crgColor: ColorRadioGroup
    var mCurrentInType = InputType.Content
    lateinit var mContentColor: String
    lateinit var mEndColor: String
    var mIsGradient: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        mContentColor = args.getString(Args_contentColor, "#37474f")
        mEndColor = args.getString(Args_endColor, "#37474f")
        mIsGradient = args.getBoolean(Args_isGradient, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("选择颜色")
        val inflate = LayoutInflater.from(context).inflate(R.layout.dialog_color, null)
        tvContentColor = inflate.findViewById(R.id.tv_contentColor)
        llContentColor = inflate.findViewById(R.id.ll_contentColor)
        vContentColor = inflate.findViewById(R.id.v_contentColor)
        llEndColor = inflate.findViewById(R.id.ll_endColor)
        vEndColor = inflate.findViewById(R.id.v_endColor)
        val sDouble = inflate.findViewById<Switch>(R.id.s_double)
        crgColor = inflate.findViewById(R.id.crg_color)

        vContentColor.backgroundTintList = ColorStateList.valueOf(Color.parseColor(mContentColor))
        vEndColor.backgroundTintList = ColorStateList.valueOf(Color.parseColor(mEndColor))

        //点击颜色按钮，设置颜色
        crgColor.setOnViewClickListener(object : ColorRadioGroup.OnViewClickListener {
            override fun onClick(str: String) {

                when (mCurrentInType) {
                    InputType.Content -> {
                        vContentColor.backgroundTintList = ColorStateList.valueOf(Color.parseColor(str))
                        mContentColor = str
                    }
                    InputType.End -> {
                        vEndColor.backgroundTintList = ColorStateList.valueOf(Color.parseColor(str))
                        mEndColor = str
                    }
                }
            }
        })

        //设置末段字段显隐动画
        llEndColor.animate().setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}

            override fun onAnimationEnd(p0: Animator?) {
            }

            override fun onAnimationCancel(p0: Animator?) {}

            override fun onAnimationStart(p0: Animator?) {
                if (llEndColor.alpha < 0.5f) {
                    llEndColor.visibility = View.VISIBLE
                } else {
                    llEndColor.visibility = View.GONE
                }
            }
        })

        //点击字段，切换颜色选择器

        llContentColor.setOnClickListener { view ->
            view.background = ContextCompat.getDrawable(context, R.drawable.btn_color_dialog)
            llEndColor.setBackgroundColor(Color.WHITE)
            mCurrentInType = InputType.Content
            crgColor.setColor(mContentColor)
        }
        llEndColor.setOnClickListener { view ->
            view.background = ContextCompat.getDrawable(context, R.drawable.btn_color_dialog)
            llContentColor.setBackgroundColor(Color.WHITE)
            mCurrentInType = InputType.End
            crgColor.setColor(mEndColor)
        }

        sDouble.isChecked = mIsGradient
        if (mIsGradient) {
            llEndColor.alpha = 1f
            llEndColor.visibility = View.VISIBLE
        } else {
            llEndColor.alpha = 0f
            llEndColor.visibility = View.GONE
        }

        //点击Switch显隐
        sDouble.setOnCheckedChangeListener { _, isChecked ->
            changeSwitch(isChecked)
        }



        builder.setView(inflate)
        builder.setNegativeButton("取消", { _: DialogInterface, _: Int ->
            run {
            }
        })
        builder.setPositiveButton("确定", { _: DialogInterface, _: Int ->
            run {
                if (parentFragment is AddDialogFragment) {
                    (parentFragment as AddDialogFragment).setColorData(mContentColor, mContentColor, mEndColor, sDouble.isChecked)
                }
            }
        })
        return builder.create()
    }

    private fun changeSwitch(isChecked: Boolean) {
        mIsGradient = isChecked
        if (isChecked) {
            tvContentColor.text = "正文和倒计时初段颜色"
            llEndColor.animate().alpha(1f)
        } else {
            tvContentColor.text = "正文和倒计时颜色"
            llEndColor.animate().alpha(0f)
            if (mCurrentInType == InputType.End) {
                llContentColor.background = ContextCompat.getDrawable(context, R.drawable.btn_color_dialog)
                llEndColor.setBackgroundColor(Color.WHITE)
                mCurrentInType = InputType.Content
                crgColor.setColor(mContentColor)
            }
        }
    }
}