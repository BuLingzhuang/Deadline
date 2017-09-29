package com.bulingzhuang.deadline.views.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.TextView
import com.bulingzhuang.deadline.R
import com.bulingzhuang.deadline.bean.DeadlineModel
import com.bulingzhuang.deadline.utils.Tools
import com.bulingzhuang.deadline.views.activitys.MainActivity
import com.bulingzhuang.deadline.views.ui.CircleProgressView
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

        var (rDay, rHour) = Tools.computeTime(currentMillis, mData.endTime)
        val (rFillDay, _) = Tools.computeTime(mData.startTime, mData.endTime)

        cpvDay.setData(rDay, rFillDay)
        cpvHour.setData(rHour, 24)
        if (rDay >= 0 && rHour >= 0) {
            if (rDay > 999) {
                rDay = 999
            }
            tvDay.text = String.format(Locale.CHINA, "%dd", rDay)
            tvHour.text = String.format(Locale.CHINA, "%dh", rHour)
            tvContent.setTextColor(ContextCompat.getColor(context, R.color.red500))
            tvType.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            tvStartDate.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            tvEndDate.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
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
        tvStartDate.text = String.format(Locale.CHINA, "%s %d时", startDay, startHour)
        tvEndDate.text = String.format(Locale.CHINA, "%s %d时", endDay, endHour)


        builder.setView(inflate)
        builder.setNegativeButton("取消", { _: DialogInterface, _: Int ->
            run {
            }
        })
        builder.setPositiveButton("编辑", { _: DialogInterface, _: Int ->
            run {

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