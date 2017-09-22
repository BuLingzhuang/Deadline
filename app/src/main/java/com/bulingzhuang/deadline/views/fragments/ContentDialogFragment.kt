package com.bulingzhuang.deadline.views.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.bulingzhuang.deadline.R
import com.bulingzhuang.deadline.bean.DeadlineModel
import com.bulingzhuang.deadline.utils.showToast
import com.bulingzhuang.deadline.views.activitys.MainActivity
import org.jetbrains.annotations.NotNull
import java.util.*

/**
 * Created by bulingzhuang
 * on 2017/9/6
 * E-mail:bulingzhuang@foxmail.com
 */
class ContentDialogFragment : DialogFragment() {

    companion object {
        val DIALOG_TYPE = "dialogType"
        val LOCAL_DATA = "deadlineModelData"
        fun newInstance(): ContentDialogFragment {
            val args = Bundle()
            args.putString(DIALOG_TYPE, DialogType.ADD.typeName)
            val fragment = ContentDialogFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(@NotNull data: DeadlineModel): ContentDialogFragment {
            val args = Bundle()
            args.putString(DIALOG_TYPE, DialogType.EDIT.typeName)
            args.putSerializable(LOCAL_DATA, data)
            val fragment = ContentDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    enum class DialogType(val typeName: String) {
        ADD("新增"), EDIT("编辑")
    }

    private lateinit var mTypeStr: String//类型
    private lateinit var mStartHour: String//开始时间
    private lateinit var mEndHour: String//结束时间
    private lateinit var typeArray: Array<String>
    private lateinit var hourArray: Array<String>
    private lateinit var mTvStartDate: TextView
    private lateinit var mTvEndDate: TextView
    private lateinit var mBtnColor: ImageView
    private lateinit var mEtContent: EditText
    private lateinit var mSpStartHour: Spinner
    private lateinit var mSpEndHour: Spinner
    private lateinit var mSpType: Spinner
    private var mLocalData: DeadlineModel? = null

    //三个颜色值
    private var mContentColor: String = "#37474f"
    private var mStartColor: String = "#37474f"
    private var mEndColor: String = "#37474f"
    //是否使用渐变色
    private var mIsGradient: Boolean = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val arguments = arguments
        val dialogType = arguments.getString(DIALOG_TYPE)
        mLocalData = if (arguments.getSerializable(LOCAL_DATA) != null) {
            arguments.getSerializable(LOCAL_DATA) as DeadlineModel
        } else {
            null
        }

        typeArray = context.resources.getStringArray(R.array.content_types)
        hourArray = context.resources.getStringArray(R.array.content_hour)
        mTypeStr = typeArray[0]
        mStartHour = hourArray[0]
        mEndHour = hourArray[0]

        val builder = AlertDialog.Builder(context)
        builder.setTitle(dialogType)
        val inflate = LayoutInflater.from(context).inflate(R.layout.dialog_content, null)
        mEtContent = inflate.findViewById(R.id.et_content)
        mBtnColor = inflate.findViewById(R.id.btn_color)
        mSpType = inflate.findViewById(R.id.sp_type)
        mSpStartHour = inflate.findViewById(R.id.sp_startHour)
        mSpEndHour = inflate.findViewById(R.id.sp_endHour)
        mTvStartDate = inflate.findViewById(R.id.tv_startDate)
        mTvEndDate = inflate.findViewById(R.id.tv_endDate)

        mBtnColor.setOnClickListener {
            ColorDialogFragment.newInstance(mIsGradient, mContentColor, mStartColor, mEndColor).show(childFragmentManager, "colorDialog")
        }

        mSpType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mTypeStr = typeArray[position]
            }
        }

        mSpStartHour.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mStartHour = hourArray[position]
            }
        }
        mSpEndHour.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mEndHour = hourArray[position]
            }
        }

        mTvStartDate.setOnClickListener {
            val fragment = DatePickerDialogFragment.newInstance(mTvStartDate.text.toString(), DatePickerDialogFragment.Type.START)
            fragment.show(childFragmentManager, "datePickerDialog")
        }
        mTvEndDate.setOnClickListener {
            val fragment = DatePickerDialogFragment.newInstance(mTvEndDate.text.toString(), DatePickerDialogFragment.Type.END)
            fragment.show(childFragmentManager, "datePickerDialog")
        }

        builder.setView(inflate)

        //根据类型处理布局

        when (dialogType) {
            DialogType.ADD.typeName -> {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                mTvStartDate.text = String.format(Locale.CHINA, "%d-09-15", year)
                mTvEndDate.text = String.format(Locale.CHINA, "%d-09-15", year + 1)
            }
            DialogType.EDIT.typeName -> {
                if (mLocalData != null) {
                    val startTime = mLocalData!!.startTime
                    val endTime = mLocalData!!.endTime
                    val startPair = formatMillis2Str(startTime)
                    val endPair = formatMillis2Str(endTime)
                    mTvStartDate.text = startPair.first
                    mSpStartHour.setSelection(startPair.second)
                    mTvEndDate.text = endPair.first
                    mSpEndHour.setSelection(endPair.second)

                    mContentColor = mLocalData!!.textColor
                    mStartColor = mLocalData!!.startColor
                    mEndColor = mLocalData!!.endColor

                    val type = mLocalData!!.type
                    val index = typeArray.indexOfFirst { it == type.typeName }
                    if (index >= 0) {
                        mSpType.setSelection(index)
                        mTypeStr = type.typeName
                    }
                    mEtContent.setText(mLocalData!!.content)
                }
                builder.setNeutralButton("删除", { _: DialogInterface, _: Int ->
                    context.showToast("删除")
                })
            }
        }

        builder.setNegativeButton("取消", { _: DialogInterface, _: Int ->
            run {}
        })
        builder.setPositiveButton("确定", { _: DialogInterface, _: Int ->
            run {
                if (activity is MainActivity) {
                    val startDate = mTvStartDate.text.toString()
                    val endDate = mTvEndDate.text.toString()
                    if (startDate.isNotEmpty() && mStartHour.isNotEmpty() && endDate.isNotEmpty() && mEndHour.isNotEmpty()) {
                        val startMillis = formatStr2Millis(startDate, mStartHour)
                        val endMillis = formatStr2Millis(endDate, mEndHour)
                        (activity as MainActivity).mPresenter.insertItem(context, mEtContent.text.toString(), mTypeStr, startMillis, endMillis, mContentColor, mStartColor, mEndColor,mIsGradient)
                    }
                }
            }
        })

        return builder.create()
    }

    private fun formatStr2Millis(@NotNull date: String, @NotNull hour: String): Long {
        val replaceHour = hour.replace(" 时", "")
        val calendar = Calendar.getInstance()
        val split = date.split("-")
        if (split.size == 3) {
            calendar.set(Calendar.YEAR, split[0].toInt())
            calendar.set(Calendar.MONTH, split[1].toInt() - 1)
            calendar.set(Calendar.DAY_OF_MONTH, split[2].toInt())
            calendar.set(Calendar.HOUR_OF_DAY, replaceHour.toInt())
        }
        return calendar.timeInMillis
    }

    /**
     * 设置结束日期
     */
    fun setDate(dateStr: String, typeName: String) {
        when (typeName) {
            DatePickerDialogFragment.Type.START.typeName -> {
                mTvStartDate.text = dateStr
            }
            DatePickerDialogFragment.Type.END.typeName -> {
                mTvEndDate.text = dateStr
            }
        }

    }

    /**
     * 设置颜色
     */
    fun setColorData(contentColor: String, startColor: String, endColor: String, isGradient: Boolean) {
        mBtnColor.setImageResource(R.drawable.ic_color_lens_black_24dp)
        mContentColor = contentColor
        mStartColor = startColor
        mEndColor = endColor
        mIsGradient = isGradient
    }

    /**
     * 时间毫秒数转换日期(yyyy-MM-dd)
     */
    private fun formatMillis2Str(time: Long): Pair<String, Int> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        val year = calendar.get(Calendar.YEAR).toString()
        val month = switchStr(calendar.get(Calendar.MONTH), true)
        val day = switchStr(calendar.get(Calendar.DAY_OF_MONTH))
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        return Pair(year + month + day, hour)
    }

    /**
     * 月、日转换str
     */
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