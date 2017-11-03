package com.bulingzhuang.deadline.views.adapter

import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bulingzhuang.deadline.R
import com.bulingzhuang.deadline.bean.DeadlineModel
import com.bulingzhuang.deadline.bean.TypeColorModel
import com.bulingzhuang.deadline.utils.Constants
import com.bulingzhuang.deadline.utils.SharePreferencesUtil
import com.bulingzhuang.deadline.utils.Tools
import com.bulingzhuang.deadline.utils.showLogE
import com.bulingzhuang.deadline.views.activity.MainActivity
import com.bulingzhuang.deadline.views.fragment.ShowDialogFragment
import com.bulingzhuang.deadline.views.ui.CircleProgressView
import com.google.gson.Gson
import org.jetbrains.annotations.NotNull
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by bulingzhuang
 * on 2017/8/30
 * E-mail:bulingzhuang@foxmail.com
 */
class DeadlineModelAdapter(context: AppCompatActivity, private var refreshTime: Long) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mContext: AppCompatActivity = context
    private var mHourOfDay: Int = 0
    private var mDayOfMonth: Int = 0
    private var mInvalidSize: Int = 0 //失效条目数量

    private val mDataList: MutableList<DeadlineModel>

    private var lastJson: String
    private lateinit var colorWork: TypeColorModel.ColorModel
    private lateinit var colorFestival: TypeColorModel.ColorModel
    private lateinit var colorBirthday: TypeColorModel.ColorModel
    private lateinit var colorFamily: TypeColorModel.ColorModel
    private lateinit var colorOther: TypeColorModel.ColorModel

    init {
        mDataList = ArrayList()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = refreshTime
        mHourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        mDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        lastJson = SharePreferencesUtil.getString(Constants.SP_DEFAULT_COLOR_DATA)
        if (lastJson.isNotEmpty()) {
            val colorModel = Gson().fromJson(lastJson, TypeColorModel::class.java)
            val typeList = colorModel.typeList
            typeList.forEach {
                when (it.typeName) {
                    DeadlineModel.Type.WORK.typeName -> {
                        colorWork = it
                    }
                    DeadlineModel.Type.FESTIVAL.typeName -> {
                        colorFestival = it
                    }
                    DeadlineModel.Type.BIRTHDAY.typeName -> {
                        colorBirthday = it
                    }
                    DeadlineModel.Type.FAMILY.typeName -> {
                        colorFamily = it
                    }
                    DeadlineModel.Type.OTHER.typeName -> {
                        colorOther = it
                    }
                    else -> {
                        colorWork = TypeColorModel.ColorModel(DeadlineModel.Type.WORK.typeName, true, "#00897b", "#c0ca33")
                        colorFestival = TypeColorModel.ColorModel(DeadlineModel.Type.FESTIVAL.typeName, true, "#4e6cef", "#00897b")
                        colorBirthday = TypeColorModel.ColorModel(DeadlineModel.Type.BIRTHDAY.typeName, true, "#8e24aa", "#4e6cef")
                        colorFamily = TypeColorModel.ColorModel(DeadlineModel.Type.FAMILY.typeName, true, "#f4511e", "#8e24aa")
                        colorOther = TypeColorModel.ColorModel(DeadlineModel.Type.OTHER.typeName, true, "#ffb300", "#dd191d")
                    }
                }
            }
        } else {
            colorWork = TypeColorModel.ColorModel(DeadlineModel.Type.WORK.typeName, true, "#00897b", "#c0ca33")
            colorFestival = TypeColorModel.ColorModel(DeadlineModel.Type.FESTIVAL.typeName, true, "#4e6cef", "#00897b")
            colorBirthday = TypeColorModel.ColorModel(DeadlineModel.Type.BIRTHDAY.typeName, true, "#8e24aa", "#4e6cef")
            colorFamily = TypeColorModel.ColorModel(DeadlineModel.Type.FAMILY.typeName, true, "#f4511e", "#8e24aa")
            colorOther = TypeColorModel.ColorModel(DeadlineModel.Type.OTHER.typeName, true, "#ffb300", "#dd191d")
        }
    }

    private fun compareColorModel() {
        val currentJson = SharePreferencesUtil.getString(Constants.SP_DEFAULT_COLOR_DATA)
        if (lastJson != currentJson) {
            lastJson = currentJson
            if (currentJson.isNotEmpty()) {
                val changeTypeList = ArrayList<String>()
                val colorModel = Gson().fromJson(currentJson, TypeColorModel::class.java)
                val typeList = colorModel.typeList
                typeList.forEach {
                    when (it.typeName) {
                        DeadlineModel.Type.WORK.typeName -> {
                            if (!Tools.compareColor(colorWork, it)) {
                                colorWork = it
                                changeTypeList.add(it.typeName)
                            }
                        }
                        DeadlineModel.Type.FESTIVAL.typeName -> {
                            if (!Tools.compareColor(colorFestival, it)) {
                                colorFestival = it
                                changeTypeList.add(it.typeName)
                            }
                        }
                        DeadlineModel.Type.BIRTHDAY.typeName -> {
                            if (!Tools.compareColor(colorBirthday, it)) {
                                colorBirthday = it
                                changeTypeList.add(it.typeName)
                            }
                        }
                        DeadlineModel.Type.FAMILY.typeName -> {
                            if (!Tools.compareColor(colorFamily, it)) {
                                colorFamily = it
                                changeTypeList.add(it.typeName)
                            }
                        }
                        DeadlineModel.Type.OTHER.typeName -> {
                            if (!Tools.compareColor(colorOther, it)) {
                                colorOther = it
                                changeTypeList.add(it.typeName)
                            }
                        }
                    }
                }
                if (changeTypeList.size > 0) {
                    changeTypeList.forEach {
                        for (position in mDataList.indices) {
                            val model = mDataList[position]
                            if (model.type.typeName == it) {
                                notifyItemChanged(position)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 检查列表（倒计时时间更新）
     */
    fun checkAdapter() {
        val calendar = Calendar.getInstance()
        val currentTimeMillis = System.currentTimeMillis()
        calendar.timeInMillis = currentTimeMillis
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        if (hourOfDay != mHourOfDay || dayOfMonth != mDayOfMonth) {
            refreshTime = currentTimeMillis
            mHourOfDay = hourOfDay
            mDayOfMonth = dayOfMonth
            var invalidSize = 0
            mDataList.forEach {
                if (it.showStatus == DeadlineModel.ShowStatus.OPEN && refreshTime > it.endTime) {
                    ++invalidSize
                }
            }
            if (invalidSize != mInvalidSize) {//失效数量发生变化了
                Collections.reverse(mDataList)
                refreshData(ArrayList<DeadlineModel>(mDataList))
            } else {
                notifyDataSetChanged()
            }
        }

        //检查颜色变更
        compareColorModel()
    }

    /**
     * 刷新列表
     */
    fun refreshData(@NotNull collection: Collection<DeadlineModel>) {
        mDataList.clear()
        var invalidSize = 0
        val validList = ArrayList<DeadlineModel>()
        collection.forEach {
            if (it.showStatus == DeadlineModel.ShowStatus.OPEN && refreshTime > it.endTime) {
                //表示失效
                mDataList.add(it)
                ++invalidSize
            } else {
                validList.add(it)
            }
        }
        mInvalidSize = invalidSize
        if (mDataList.size > 0) {
            mDataList.add(DeadlineModel(-1, "", DeadlineModel.Type.OTHER, 0, 0, DeadlineModel.ShowStatus.VALID))
        }
        mDataList.addAll(validList)
        Collections.reverse(mDataList)
//            mDataList.add(DeadlineModel())
        notifyDataSetChanged()
    }

    /**
     * 添加一条数据
     */
    fun addItem(@NotNull collection: Collection<DeadlineModel>) {
        mDataList.addAll(0, collection)
        notifyItemRangeInserted(0, collection.size)
    }

    /**
     * 删除一条数据
     */
    fun delItem(_id: Long, showSnakeBar: Boolean) {
        for (position in mDataList.indices) {
            val item = mDataList[position]
            if (item._id == _id) {
                if (mContext is MainActivity && mDataList.remove(item)) {
                    notifyItemRemoved(position)
                    if (showSnakeBar) {
                        //失效数据不可撤销，只显示删除成功
                        if (item.showStatus == DeadlineModel.ShowStatus.OPEN && refreshTime > item.endTime) {
                            (mContext as MainActivity).showSnakeBar("删除成功")
                        } else {
                            (mContext as MainActivity).showSnakeBarWithAction("删除成功", item)
                        }
                    }
                    break
                }
            }
        }
    }

    /**
     * 编辑一条数据
     */
    fun editItem(@NotNull last_id: Long, @NotNull collection: Collection<DeadlineModel>) {
        collection.forEach {
            for (position in mDataList.indices) {
                if (mDataList[position]._id == last_id) {
                    mDataList[position] = it
                    notifyItemChanged(position)
                    break
                }
            }
        }
    }

    /**
     * 获取当前列表中最大id
     */
    fun getMaxId(): Long {
        return mDataList.map { it._id }.max() ?: 0L
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val item = mDataList[position]
        val itemColor = when (item.type) {
            DeadlineModel.Type.WORK -> {
                colorWork
            }
            DeadlineModel.Type.FESTIVAL -> {
                colorFestival
            }
            DeadlineModel.Type.BIRTHDAY -> {
                colorBirthday
            }
            DeadlineModel.Type.FAMILY -> {
                colorFamily
            }
            DeadlineModel.Type.OTHER -> {
                colorOther
            }
        }
        showLogE("数据：$item")
        when (holder?.itemViewType) {
            R.layout.adapter_main_open -> {
                val openHolder = holder as DeadlineModelAdapterViewHolderOpen
                var (rDay, rHour) = Tools.computeTime(Math.max(refreshTime,item.startTime), item.endTime)
                val (rFillDay, _) = Tools.computeTime(item.startTime, item.endTime)
                showLogE("rDay=$rDay,rHour=$rHour,rFillDay=$rFillDay")
                openHolder.mCpvDay.setData(rDay, rFillDay)
                openHolder.mCpvHour.setData(rHour, 24)
                if (rDay >= 0 && rHour >= 0) {
                    if (rDay > 999) {
                        rDay = 999
                    }
                    openHolder.mTvDay.text = String.format(Locale.CHINA, "%dd", rDay)
                    openHolder.mTvHour.text = String.format(Locale.CHINA, "%dh", rHour)
                    openHolder.mTvContent.setTextColor(Color.parseColor(itemColor.contentColor))
                    openHolder.mTvEndTime.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                    if (itemColor.isGradient) {
                        openHolder.mCpvDay.setColor(itemColor.contentColor, itemColor.endColor)
                        openHolder.mCpvHour.setColor(itemColor.contentColor, itemColor.endColor)
                    } else {
                        openHolder.mCpvDay.setColor(itemColor.contentColor)
                        openHolder.mCpvHour.setColor(itemColor.contentColor)
                    }
                } else {
                    openHolder.mTvDay.text = ""
                    openHolder.mTvHour.text = ""
                    openHolder.mTvContent.setTextColor(ContextCompat.getColor(mContext, R.color.invalid_gray))
                    openHolder.mTvEndTime.setTextColor(ContextCompat.getColor(mContext, R.color.invalid_gray))
                }
                openHolder.mTvContent.text = item.content
                val (endDay, endHour) = Tools.formatMillis2Str(item.endTime)
                openHolder.mTvEndTime.text = String.format(Locale.CHINA, "至 %s %d时", endDay, endHour)
                openHolder.itemView.setOnClickListener {
                    ShowDialogFragment.newInstance(refreshTime, item).show(mContext.supportFragmentManager, "showDialog")
                }
            }
            R.layout.adapter_main_valid -> {

            }
        }
    }


    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val inflate = LayoutInflater.from(mContext).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.adapter_main_open -> {
                DeadlineModelAdapterViewHolderOpen(inflate)
            }
            R.layout.adapter_main_valid -> {
                DeadlineModelAdapterViewHolderEmpty(inflate)
            }
            else -> {
                DeadlineModelAdapterViewHolderEmpty(inflate)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val itemData = mDataList[position]
        return when (itemData.showStatus) {
            DeadlineModel.ShowStatus.CLOSE -> R.layout.adapter_main_close
            DeadlineModel.ShowStatus.OPEN -> R.layout.adapter_main_open
            DeadlineModel.ShowStatus.VALID -> R.layout.adapter_main_valid
        }
    }


    /**
     * 展开状态ViewHolder
     */
    private inner class DeadlineModelAdapterViewHolderOpen(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mCpvDay: CircleProgressView = itemView.findViewById(R.id.cpv_day)
        val mCpvHour: CircleProgressView = itemView.findViewById(R.id.cpv_hour)
        val mTvDay: TextView = itemView.findViewById(R.id.tv_day)
        val mTvHour: TextView = itemView.findViewById(R.id.tv_hour)
        val mTvContent: TextView = itemView.findViewById(R.id.tv_content)
        //        val mTvType: TextView = itemView.findViewById(R.id.tv_type)
//        val mTvStartTime: TextView = itemView.findViewById(R.id.tv_startTime)
        val mTvEndTime: TextView = itemView.findViewById(R.id.tv_endTime)
//        val mBtnEdit: ImageView = itemView.findViewById(R.id.btn_edit)
//        val mBtnDel: ImageView = itemView.findViewById(R.id.btn_del)
    }

    private inner class DeadlineModelAdapterViewHolderEmpty(itemView: View) : RecyclerView.ViewHolder(itemView)
}