package com.bulingzhuang.deadline.views.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bulingzhuang.deadline.R
import com.bulingzhuang.deadline.bean.DeadlineModel
import com.bulingzhuang.deadline.views.ui.CircleProgressView
import org.jetbrains.annotations.NotNull
import java.util.*

/**
 * Created by bulingzhuang
 * on 2017/8/30
 * E-mail:bulingzhuang@foxmail.com
 */
class DeadlineModelAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mDataList: MutableList<DeadlineModel>

    init {
        mDataList = ArrayList()
    }

    /**
     * 刷新列表
     */
    fun refreshData(@NotNull collection: Collection<DeadlineModel>) {

        if (mDataList.size == 0) {
            mDataList.addAll(collection)
            Collections.reverse(mDataList)
//            mDataList.add(DeadlineModel())
            notifyItemRangeInserted(0, collection.size)
        } else {
            mDataList.clear()
            mDataList.addAll(collection)
            Collections.reverse(mDataList)
//            mDataList.add(DeadlineModel())
            notifyDataSetChanged()
        }
    }

    fun addData(@NotNull collection: Collection<DeadlineModel>) {
        mDataList.addAll(0, collection)
        notifyItemRangeInserted(0, collection.size)
    }

    /**
     * 获取当前列表中最大id
     */
    fun getMaxId(): Long {
        return mDataList.map { it._id }.max() ?: 0L
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val item = mDataList[position]
        when (holder?.itemViewType) {
            R.layout.adapter_main_close -> {
                val closeHolder = holder as DeadlineModelAdapterViewHolderClose
//                if (refreshTime > item.endTime) {
                val (rDay, rHour) = computeTime(item.startTime, item.endTime)
                val (rFillDay, rFillHour) = computeTime(item.startTime, item.endTime)
//                closeHolder.mCpvDay.setData(rDay, rFillDay)
//                closeHolder.mCpvHour.setData(rHour, rFillHour)
                closeHolder.mCpvDay.setData(rDay, 30)
                closeHolder.mCpvHour.setData(rHour, 24)
                closeHolder.mTvDay.text = String.format(Locale.CHINA, "%sd", rDay)
                closeHolder.mTvHour.text = String.format(Locale.CHINA, "%sh", rHour)
//
//                } else {
//                    //倒计时已经过时
//                    closeHolder.mCpvDay.setData(0, 1)
//                    closeHolder.mCpvHour.setData(0, 1)
//                    closeHolder.mTvDay.text = "0d"
//                    closeHolder.mTvHour.text = "0h"
//                }
                closeHolder.mTvContent.text = item.content
            }
            R.layout.adapter_main_open -> {
                val openHolder = holder as DeadlineModelAdapterViewHolderOpen
                val (rDay, rHour) = computeTime(item.startTime, item.endTime)
                openHolder.mCpvDay.setData(rDay, 30)
                openHolder.mCpvHour.setData(rHour, 24)
                openHolder.mTvDay.text = String.format(Locale.CHINA, "%sd", rDay)
                openHolder.mTvHour.text = String.format(Locale.CHINA, "%sh", rHour)
                openHolder.mTvContent.text = item.content
                openHolder.mTvType.text = "类型：${item.type.typeName}"
                openHolder.mTvStartTime.text = item.startTime.toString()
                openHolder.mTvEndTime.text = item.endTime.toString()
            }
            R.layout.adapter_main_empty -> {

            }
        }
    }

    private val hourPara = 1000 * 60 * 60
    /**
     * 计算剩余时间
     */
    private fun computeTime(sDate: Long, eDate: Long): Pair<Int, Int> {
        val remainingD = eDate - sDate
        val rDay = remainingD / hourPara / 24
        val rHour = remainingD / hourPara - rDay * 24
        return Pair(rDay.toInt(), rHour.toInt())
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val inflate = LayoutInflater.from(context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.adapter_main_close -> {
                DeadlineModelAdapterViewHolderClose(inflate)
            }
            R.layout.adapter_main_open -> {
                DeadlineModelAdapterViewHolderOpen(inflate)
            }
            R.layout.adapter_main_empty -> {
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
            DeadlineModel.ShowStatus.EMPTY -> R.layout.adapter_main_empty
        }
    }


    /**
     * 未展开状态ViewHolder
     */
    private inner class DeadlineModelAdapterViewHolderClose(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mCpvDay: CircleProgressView = itemView.findViewById(R.id.cpv_day)
        val mCpvHour: CircleProgressView = itemView.findViewById(R.id.cpv_hour)
        val mTvDay: TextView = itemView.findViewById(R.id.tv_day)
        val mTvHour: TextView = itemView.findViewById(R.id.tv_hour)
        val mTvContent: TextView = itemView.findViewById(R.id.tv_content)
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
        val mTvType: TextView = itemView.findViewById(R.id.tv_type)
        val mTvStartTime: TextView = itemView.findViewById(R.id.tv_startTime)
        val mTvEndTime: TextView = itemView.findViewById(R.id.tv_endTime)
        val mBtnEdit: ImageView = itemView.findViewById(R.id.btn_edit)
        val mBtnDel: ImageView = itemView.findViewById(R.id.btn_del)
    }

    private inner class DeadlineModelAdapterViewHolderEmpty(itemView: View) : RecyclerView.ViewHolder(itemView)
}