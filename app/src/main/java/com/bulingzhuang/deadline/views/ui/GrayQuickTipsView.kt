package com.bulingzhuang.deadline.views.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.bulingzhuang.deadline.R

/**
 * Created by blz
 * on 2017/7/31
 * E-mail:bulingzhuang@foxmail.com
 */
class GrayQuickTipsView : View {

    //文字背景圆角矩形RectF
    private val mTextBgRectF = RectF()
    //背景画笔
    private val mBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    //文字画笔
    private val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    //文字高度
    private var mTextYOffset: Float = 0.0f
    //数据
    private var mDataList: ArrayList<TipsEntity> = ArrayList()
    //当前设备密度
    private var mDensity: Float = resources.displayMetrics.density
    //item间隔
    private var mMarginX: Float = 0.0f
    private var mMarginY: Float = 0.0f
    //item文字边距
    private var mPaddingX: Float = 0.0f
    private var mPaddingY: Float = 0.0f
    //上一次x、y所在位置
    private var mLastX: Float = 0.0f
    private var mLastY: Float = 0.0f
    //绘制行数
    private var mLines: Int = 0
    //历史图标Bitmap
    private var mIconBitmap: Bitmap? = null
    //图标 宽度
    private var mIconPadding = 0f

    //touch到的item
    private var mTouchItem: TipsEntity? = null

    private var mOnClickListener: OnViewClickListener? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttr(context, attrs)
    }

    /**
     * 初始化参数
     */
    private fun initAttr(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.GrayQuickTipsView)
        val iconId = typedArray.getResourceId(R.styleable.GrayQuickTipsView_historyIcon, R.mipmap.ic_launcher_foreground)
        if (iconId != R.mipmap.ic_launcher_foreground) {
            val realBitmap = BitmapFactory.decodeResource(resources, iconId)
            val matrix = Matrix()
            val maxLength = Math.max(realBitmap.width, realBitmap.height)
            val scale = mDensity * 12.5f / maxLength
            matrix.postScale(scale, scale)
            mIconBitmap = Bitmap.createBitmap(realBitmap, 0, 0, realBitmap.width, realBitmap.height, matrix, true)
        }
        mIconPadding = mIconBitmap?.let { mIconBitmap!!.width + mPaddingX * 0.4f } ?: 0f
        typedArray.recycle()
    }

    /**
     * 初始化
     */
    init {
        mMarginY = mDensity * 18.5f
        mMarginX = mDensity * 13.5f
        mPaddingY = mDensity * 12f
        mPaddingX = mDensity * 14.5f
        mTextPaint.textSize = mDensity * 12.5f
        mTextPaint.color = Color.BLACK
        val fontMetrics = mTextPaint.fontMetrics
        mTextYOffset = -fontMetrics.ascent - fontMetrics.descent
        mBgPaint.color = Color.parseColor("#eeeeee")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mLastX = 0.0f
        val itemHeight = mMarginY * 2 + mPaddingY * 2 + mTextYOffset
        mLines = (height / itemHeight).toInt()
        mLastY = height.toFloat() - itemHeight
        //画文字
        mDataList.forEach {
            val availableX = width - mLastX
            val itemX = if (it.showIcon) {
                mMarginX + mPaddingX * 2 + mTextPaint.measureText(it.content) + mIconPadding
            } else {
                mMarginX + mPaddingX * 2 + mTextPaint.measureText(it.content)
            }
            //当前条目宽度大于可用宽度，需要换行
            if (itemX > availableX) {
                mLastX = 0.0f
                mLastY -= mMarginY + mPaddingY * 2 + mTextYOffset
                --mLines
//                Log.e("blz", "接下来要开始的Y = $mLastY，实际控件高度height = $height,第$mLines 行")
            }
            if (mLines >= 0) {
                it.left = mLastX + mMarginX
                it.top = mLastY + mMarginY
                it.right = mLastX + itemX
                it.bottom = mLastY + mMarginY + mPaddingY * 2 + mTextYOffset
                val radius = mPaddingY + mTextYOffset / 2
                val isTouch = if (mTouchItem != null) {
                    mTouchItem!!.left == it.left && mTouchItem!!.top == it.top && mTouchItem!!.right == it.right && mTouchItem!!.bottom == it.bottom
                } else {
                    false
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (isTouch) {
                        mBgPaint.color = Color.parseColor("#cccccc")
                    }
                    canvas?.drawRoundRect(it.left, it.top, it.right, it.bottom, radius, radius, mBgPaint)
                    if (isTouch) {
                        mBgPaint.color = Color.parseColor("#eeeeee")
                    }
                } else {
                    mTextBgRectF.set(it.left, it.top, it.right, it.bottom)
                    canvas?.drawRoundRect(mTextBgRectF, radius, radius, mBgPaint)
                }
                if (mIconBitmap != null && it.showIcon) {
                    val offset = (mIconBitmap!!.width.toFloat() - mTextYOffset) / 2f
                    canvas?.drawBitmap(mIconBitmap!!, mLastX + mMarginX + mPaddingX, mLastY + mMarginY + mPaddingY - offset, mTextPaint)
                }
                if (it.showIcon) {
                    canvas?.drawText(it.content, mLastX + mMarginX + mPaddingX + mIconPadding, mLastY + mMarginY + mPaddingY + mTextYOffset, mTextPaint)
                } else {
                    canvas?.drawText(it.content, mLastX + mMarginX + mPaddingX, mLastY + mMarginY + mPaddingY + mTextYOffset, mTextPaint)
                }
                mLastX += itemX
            }
        }
    }

    interface OnViewClickListener {
        fun onClick(str: String)
    }

    fun setOnViewClickListener(listener: OnViewClickListener) {
        mOnClickListener = listener
    }

    //按下的时候记录的x、y值
    private var mDownX: Float = 0.0f
    private var mDownY: Float = 0.0f


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            val x = event.x
            val y = event.y
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mDownX = x
                    mDownY = y
//                    Log.e("blz", "Touch状态：DOWN，x=$x，y=$y")
                    mDataList.filter { it.left <= mDownX && it.right >= mDownX && it.top <= mDownY && it.bottom >= mDownY }
                            .forEach {
                                mTouchItem = it
                                invalidate()
                            }
//                    invalidate()
                }
                MotionEvent.ACTION_UP -> {
//                    Log.e("blz", "Touch状态：UP，x=$x，y=$y")
                    val absX = Math.abs(x - mDownX)
                    val absY = Math.abs(y - mDownY)
                    if (absX < 7 * mDensity && absY < 7 * mDensity) {
//                        Log.e("blz", "触发点击事件")
                        mDataList.filter { it.left <= mDownX && it.right >= mDownX && it.top <= mDownY && it.bottom >= mDownY }
                                .forEach {
                                    it.realContent?.let { it1 -> mOnClickListener?.onClick(it1) }
                                }
                    }
                    mTouchItem = null
                    invalidate()
                }
                else -> {
//                    Log.e("blz", "Touch状态：其他状态(${event.action})，x=$x，y=$y")
                }
            }
        }
        return true
    }

    /**
     * 传入数据
     */
    fun setData(historyList: ArrayList<String>?, searchList: ArrayList<String>?) {
        mapDataList(historyList, true, true)
        mapDataList(searchList, false)
        invalidate()
    }

    /**
     * 处理数据，对超长字符串截取
     */
    private fun mapDataList(data: ArrayList<String>?, showIcon: Boolean, clear: Boolean = false) {
        if (clear) {
            mDataList.clear()
        }
        data?.forEach {
            when {
                it.length > 6 -> {
                    val str = it.substring(0, 6) + "…"
                    mDataList.add(TipsEntity(str, it, showIcon))
                }
                it.length == 1 -> mDataList.add(TipsEntity("    $it    ", it, showIcon))
                it.length == 2 -> mDataList.add(TipsEntity("  $it  ", it, showIcon))
                else -> mDataList.add(TipsEntity(it, it, showIcon))
            }
        }
    }

    private class TipsEntity(var content: String?, var realContent: String?, var showIcon: Boolean = false, var left: Float = 0.0f, var top: Float = 0.0f, var right: Float = 0.0f, var bottom: Float = 0.0f)
}