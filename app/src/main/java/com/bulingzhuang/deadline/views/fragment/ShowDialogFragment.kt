package com.bulingzhuang.deadline.views.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.TextView
import com.bulingzhuang.deadline.R
import com.bulingzhuang.deadline.bean.DeadlineModel
import com.bulingzhuang.deadline.bean.TypeColorModel
import com.bulingzhuang.deadline.utils.Constants
import com.bulingzhuang.deadline.utils.SharePreferencesUtil
import com.bulingzhuang.deadline.utils.Tools
import com.bulingzhuang.deadline.views.activity.MainActivity
import com.bulingzhuang.deadline.views.ui.CircleProgressView
import com.google.gson.Gson
import java.util.*

/**
 * Created by bulingzhuang
 * on 2017/9/28
 * E-mail:bulingzhuang@foxmail.com
 */
class ShowDialogFragment : DialogFragment() {
    companion object {
        fun newInstance(timeMillis: Long, data: DeadlineModel): ShowDialogFragment {
            val fragment = ShowDialogFragment()
            val args = Bundle()
            args.putSerializable("data", data)
            args.putLong("timeMillis", timeMillis)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var mData: DeadlineModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
        val inflate = LayoutInflater.from(context).inflate(R.layout.dialog_show, null)
        val cpvDay = inflate.findViewById<CircleProgressView>(R.id.cpv_day)
        val tvDay = inflate.findViewById<TextView>(R.id.tv_day)
        val cpvHour = inflate.findViewById<CircleProgressView>(R.id.cpv_hour)
        val tvHour = inflate.findViewById<TextView>(R.id.tv_hour)
        val tvContent = inflate.findViewById<TextView>(R.id.tv_content)
        val tvType = inflate.findViewById<TextView>(R.id.tv_type)
        val tvStartDate = inflate.findViewById<TextView>(R.id.tv_startDate)
        val tvEndDate = inflate.findViewById<TextView>(R.id.tv_endDate)

        val currentMillis = arguments.getLong("timeMillis")

        mData = arguments.getSerializable("data") as DeadlineModel

        var (rDay, rHour) = Tools.computeTime(Math.max(currentMillis, mData.startTime), mData.endTime)
        val (rFillDay, _) = Tools.computeTime(mData.startTime, mData.endTime)

        val data = SharePreferencesUtil.getString(Constants.SP_DEFAULT_COLOR_DATA)
        val colorList = if (data.isNotEmpty()) {
            val colorModel = Gson().fromJson(data, TypeColorModel::class.java)
            colorModel.typeList
        } else {
            val list = ArrayList<TypeColorModel.ColorModel>()
            list.add(TypeColorModel.ColorModel(DeadlineModel.Type.WORK.typeName, true, "#c0ca33", "#00897b"))
            list.add(TypeColorModel.ColorModel(DeadlineModel.Type.FESTIVAL.typeName, true, "#00897b", "#4e6cef"))
            list.add(TypeColorModel.ColorModel(DeadlineModel.Type.BIRTHDAY.typeName, true, "#4e6cef", "#8e24aa"))
            list.add(TypeColorModel.ColorModel(DeadlineModel.Type.FAMILY.typeName, true, "#8e24aa", "#f4511e"))
            list.add(TypeColorModel.ColorModel(DeadlineModel.Type.OTHER.typeName, true, "#f4511e", "#fdd835"))
            list
        }

        var model: TypeColorModel.ColorModel? = null
        colorList.filter { it.typeName == mData.type.typeName }.forEach { model = it }

        cpvDay.setData(rDay, rFillDay)
        cpvHour.setData(rHour, 24)
        if (rDay >= 0 && rHour >= 0) {
            if (rDay > 999) {
                rDay = 999
            }
            tvDay.text = String.format(Locale.CHINA, "%dd", rDay)
            tvHour.text = String.format(Locale.CHINA, "%dh", rHour)
            tvContent.setTextColor(Color.parseColor(model?.contentColor))
            tvType.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            tvStartDate.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            tvEndDate.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            if (model != null) {
                if (model!!.isGradient) {
                    cpvDay.setColor(model!!.contentColor, model!!.endColor)
                    cpvHour.setColor(model!!.contentColor, model!!.endColor)
                } else {
                    cpvDay.setColor(model!!.contentColor)
                    cpvHour.setColor(model!!.contentColor)
                }
            }
        } else {
            tvDay.text = ""
            tvHour.text = ""
            tvContent.setTextColor(ContextCompat.getColor(context, R.color.invalid_gray))
            tvType.setTextColor(ContextCompat.getColor(context, R.color.invalid_gray))
            tvStartDate.setTextColor(ContextCompat.getColor(context, R.color.invalid_gray))
            tvEndDate.setTextColor(ContextCompat.getColor(context, R.color.invalid_gray))
        }
        tvContent.text = mData.content
        tvType.text = String.format(Locale.CHINA, "类型：%s", mData.type.typeName)
        val (startDay, startHour) = Tools.formatMillis2Str(mData.startTime)
        val (endDay, endHour) = Tools.formatMillis2Str(mData.endTime)
        val startHourStr = if (startHour < 10) {
            "  $startHour"
        } else {
            startHour.toString()
        }
        val endHourStr = if (endHour < 10) {
            "  $endHour"
        } else {
            endHour.toString()
        }
        tvStartDate.text = String.format(Locale.CHINA, "%s %s时", startDay, startHourStr)
        tvEndDate.text = String.format(Locale.CHINA, "%s %s时", endDay, endHourStr)


        builder.setView(inflate)
        builder.setNegativeButton("取消", { _: DialogInterface, _: Int ->
            run {
            }
        })
        builder.setPositiveButton("编辑", { _: DialogInterface, _: Int ->
            run {
                if (activity is MainActivity) {
                    (activity as MainActivity).mPresenter.showDialog((activity as MainActivity), mData)
                }
            }
        })
        builder.setNeutralButton("删除", { _: DialogInterface, _: Int ->
            run {
                val fragmentActivity = activity
                if (fragmentActivity is MainActivity) {
                    fragmentActivity.mPresenter.delItem(fragmentActivity, mData._id)
                }
            }
        })

        return builder.create()
    }
}