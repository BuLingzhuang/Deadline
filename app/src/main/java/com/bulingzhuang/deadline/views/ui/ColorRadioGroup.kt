package com.bulingzhuang.deadline.views.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.bulingzhuang.deadline.R
import com.bulingzhuang.deadline.utils.showLogE

/**
 * Created by bulingzhuang
 * on 2017/9/14
 * E-mail:bulingzhuang@foxmail.com
 */
class ColorRadioGroup : View {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttr(context, attrs)
        init(context)
    }

    private var mDensity: Float = 0f//屏幕密度
    private lateinit var mColorList: Array<String>
    private var mCheckPosition = -1
    private var mListener: OnViewClickListener? = null

    private val mCheckPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mCheckPath = Path()
    private val mIconPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mIconAlphaPaint = Paint(Paint.ANTI_ALIAS_FLAG)


    companion object {
        private val mColumnCount = 4//列数
    }

    interface OnViewClickListener {
        fun onClick(str: String)
    }

    fun setOnViewClickListener(listener: OnViewClickListener) {
        mListener = listener
    }

    /**
     * 传入颜色
     */
     fun setColor(color: String) {
        mCheckPosition = mColorList.indices.firstOrNull { mColorList[it] == color } ?: -1
        invalidate()
    }

    /**
     * 初始化
     */
    private fun init(context: Context) {
        mCheckPaint.style = Paint.Style.FILL
        mCheckPaint.color = Color.WHITE
        val colorArray = context.resources.getStringArray(R.array.colorRadioGroup_color)
        mColorList = if (colorArray != null && colorArray.isNotEmpty()) {
            colorArray
        } else {
            arrayOf("#37474f")
        }
    }

    /**
     * 初始化参数
     */
    private fun initAttr(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorRadioGroup)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = setMeasureSize(widthMeasureSpec, 40f)
        val height = setMeasureSize(heightMeasureSpec, 40f)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            drawIcon(canvas)
        }
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
//                    showLogE("点击位置：x=$mDownX，y=$mDownY")
                }
                MotionEvent.ACTION_MOVE -> {

                }
                MotionEvent.ACTION_UP -> {
                    val absX = Math.abs(x - mDownX)
                    val absY = Math.abs(y - mDownY)
//                    showLogE("点击位置：x=$x，y=$y，移动了($absX,$absY)")
                    if (absX < dp2px(7f) && absY < dp2px(7f)) {
                        //触发点击事件
                        val centerY = height.toFloat() / 2
                        val centerX = width.toFloat() / 2
//                        showLogE("中心点：x=$centerX,y=$centerY")
                        val itemLen = Math.min(centerX, centerY) / 2
//                        showLogE("每块边长：itemLen=$itemLen")
                        //竖向排列，X表示第几排，Y表示第几个(根据是否reverse处理)
                        val positionX = (mDownX / itemLen).toInt()
                        val positionY = (mDownY / itemLen).toInt()
                        //为true表示反向了
                        val reverse = positionX % 2 != 1
                        mCheckPosition = if (reverse) {
                            positionX * mColumnCount + positionY
                        } else {
                            positionX * mColumnCount + (3 - positionY)
                        }
//                        showLogE("点击了：$mCheckPosition")
                        mListener?.onClick(mColorList[mCheckPosition])
                        invalidate()
                    }
                }
            }
        }
        return true
    }

    /**
     * 绘制颜色选择按钮
     */
    private fun drawIcon(canvas: Canvas) {
        //分为四列
        val centerY = height.toFloat() / 2
        val centerX = width.toFloat() / 2
        val itemLen = Math.min(centerX, centerY) / 2
        for (index in mColorList.indices) {
            val column = index / mColumnCount
            //为true表示需要反向绘制的行
            val reverse = column % 2 == 1
            val line = index % mColumnCount
            mIconPaint.color = Color.parseColor(mColorList[index])
            val itemCenterX = centerX + (column - 1.5f) * itemLen
            if (index == mCheckPosition) {
                val colorSb = StringBuilder(mColorList[index])
                colorSb.insert(1, "66")
                mIconAlphaPaint.color = Color.parseColor(colorSb.toString())
                val itemCenterY = if (reverse) {
                    canvas.drawCircle(itemCenterX, centerY - (line - 1.5f) * itemLen, itemLen * 0.86f / 2, mIconAlphaPaint)
                    canvas.drawCircle(itemCenterX, centerY - (line - 1.5f) * itemLen, itemLen * 0.66f / 2, mIconPaint)
                    centerY - (line - 1.5f) * itemLen
                } else {
                    canvas.drawCircle(itemCenterX, centerY + (line - 1.5f) * itemLen, itemLen * 0.86f / 2, mIconAlphaPaint)
                    canvas.drawCircle(itemCenterX, centerY + (line - 1.5f) * itemLen, itemLen * 0.66f / 2, mIconPaint)
                    centerY + (line - 1.5f) * itemLen
                }
                drawCheck(canvas, itemCenterX, itemCenterY, itemLen)
            } else {
                if (reverse) {
                    canvas.drawCircle(itemCenterX, centerY - (line - 1.5f) * itemLen, itemLen * 0.86f / 2, mIconPaint)
                } else {
                    canvas.drawCircle(itemCenterX, centerY + (line - 1.5f) * itemLen, itemLen * 0.86f / 2, mIconPaint)
                }
            }
        }
    }

    /**
     * 绘制对勾
     */
    private fun drawCheck(canvas: Canvas, centerX: Float, centerY: Float, maxLen: Float) {
        canvas.save()
//        val itemLen = maxLen * 0.27f / 9
//        mCheckPath.reset()
//        mCheckPath.moveTo(centerX + 6f * itemLen, centerY - 3.5f * itemLen)
//        mCheckPath.lineTo(centerX - 2f * itemLen, centerY + 4.5f * itemLen)
//        mCheckPath.lineTo(centerX - 6f * itemLen, centerY + 0.5f * itemLen)
//        mCheckPath.lineTo(centerX - 5f * itemLen, centerY - 0.5f * itemLen)
//        mCheckPath.lineTo(centerX - 2f * itemLen, centerY + 2.5f * itemLen)
//        mCheckPath.lineTo(centerX + 5f * itemLen, centerY - 4.5f * itemLen)
//        mCheckPath.close()
        val itemLen = maxLen * 0.3f / 10
        mCheckPath.reset()
        mCheckPath.moveTo(centerX + 6f * itemLen, centerY - 3f * itemLen)
        mCheckPath.lineTo(centerX - 2f * itemLen, centerY + 5f * itemLen)
        mCheckPath.lineTo(centerX - 6f * itemLen, centerY + 1f * itemLen)
        mCheckPath.lineTo(centerX - 5f * itemLen, centerY - 1f * itemLen)
        mCheckPath.lineTo(centerX - 2f * itemLen, centerY + 1f * itemLen)
        mCheckPath.lineTo(centerX + 5f * itemLen, centerY - 5f * itemLen)
        mCheckPath.close()
        canvas.drawPath(mCheckPath, mCheckPaint)
        canvas.restore()
    }

    /**
     * dp转px
     */
    private fun dp2px(dpValue: Float): Int {
        if (mDensity == 0f) {
            mDensity = context.resources.displayMetrics.density
        }
        return (mDensity * dpValue + 0.5f).toInt()
    }

    /**
     * 测量尺寸
     */
    private fun setMeasureSize(measureSpec: Int, size: Float): Int {
        val mode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        return when (mode) {
            MeasureSpec.EXACTLY -> specSize
            MeasureSpec.AT_MOST -> minOf(specSize, dp2px(size))
            MeasureSpec.UNSPECIFIED -> dp2px(size)
            else -> dp2px(size)
        }
    }
}