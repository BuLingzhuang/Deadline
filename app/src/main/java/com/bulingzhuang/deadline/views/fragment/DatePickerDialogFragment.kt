package com.bulingzhuang.deadline.views.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.DatePicker
import java.util.*

/**
 * Created by bulingzhuang
 * on 2017/9/7
 * E-mail:bulingzhuang@foxmail.com
 */
class DatePickerDialogFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    companion object {
        private val DATE_STR = "dateStr"
        private val TYPE = "type"
        fun newInstance(dateStr: String, type: Type): DatePickerDialogFragment {
            val args = Bundle()
            args.putString(DATE_STR, dateStr)
            args.putString(TYPE, type.typeName)
            val fragment = DatePickerDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    enum class Type(val typeName: String) {
        START("start"), END("end")
    }

    private lateinit var mTypeName:String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dateStr = arguments.getString(DATE_STR)
        mTypeName = arguments.getString(TYPE,Type.START.typeName)
        val calendar = Calendar.getInstance()
        if (dateStr.isNotEmpty()) {
            val split = dateStr.split("-")
            if (split.size == 3) {
                calendar.set(Calendar.YEAR, split[0].toInt())
                calendar.set(Calendar.MONTH, split[1].toInt() - 1)
                calendar.set(Calendar.DAY_OF_MONTH, split[2].toInt())
            }
        }
        return DatePickerDialog(context, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    }

    override fun onDateSet(dp: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        if (parentFragment is AddDialogFragment) {
            (parentFragment as AddDialogFragment).setDate(year.toString() + switchStr(month, true) + switchStr(dayOfMonth),mTypeName)
        }
    }

    private fun switchStr(int: Int, needAdd: Boolean = false): String {
        val result: Int = if (needAdd) {
            int + 1
        } else {
            int
        }
        return if (result < 10) {
            "-0" + result.toString()
        } else {
            "-" + result.toString()
        }
    }
}