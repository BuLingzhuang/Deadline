package com.bulingzhuang.deadline.views.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.bulingzhuang.deadline.R

/**
 * Created by bulingzhuang
 * on 2017/8/24
 * E-mail:bulingzhuang@foxmail.com
 */
class TEMPView : View {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttr(context, attrs)
    }

    private var temp: Float = 0f
    private var maxTemp: Float = 0f
    private val mWhitePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mColorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mYOffset: Float = 0f
    private var mIsNegative: Boolean = false//是否是零下温度

    companion object {
        val negativeStr = "-"
    }

    /**
     * 刷新温度
     */
    fun refreshTempData(tempNum: Int, isNegative: Boolean = false) {
        mIsNegative = isNegative
        val tempNumF = tempNum + 0.6f
        maxTemp = tempNumF
        val duration = tempNum * 4666 / 30 + 233f
        val anim = ObjectAnimator.ofFloat(this, "temp", tempNumF)
        anim.duration = Math.min(duration, 4666f).toLong()
        anim.interpolator = AccelerateDecelerateInterpolator()
        anim.start()
    }

    fun setTemp(tempNum: Float) {
        this.temp = tempNum
        invalidate()
    }

    private fun initAttr(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TEMPView)
        temp = typedArray.getInt(R.styleable.TEMPView_temp, 0) + 0.6f
        val textColor = typedArray.getColor(R.styleable.TEMPView_textColor, ContextCompat.getColor(context, R.color.colorPrimary))
        val textSize = typedArray.getDimension(R.styleable.TEMPView_textSize, 20f)
        typedArray.recycle()
        mWhitePaint.textSize = textSize
        mWhitePaint.color = Color.WHITE
        val font = ResourcesCompat.getFont(context, R.font.pingfang_light)
        mWhitePaint.typeface = font
        mWhitePaint.isFakeBoldText = true
        mColorPaint.textSize = textSize
        mColorPaint.color = textColor
        mColorPaint.typeface = font
        mColorPaint.isFakeBoldText = true

        mYOffset = -(mWhitePaint.fontMetrics.ascent + mWhitePaint.fontMetrics.descent) / 2f
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
            //整数部分作为实际显示数字，小数部分表示偏移量
            val tempInt = temp.toInt()
            val tempDecimal = temp - tempInt
            val tempStr = tempInt.toString()
            val measureText = mWhitePaint.measureText(tempStr)
            var negativeWidth = 0f
            val centerY = height.toFloat() / 2 + mYOffset
            val textY = if (tempInt == maxTemp.toInt() && tempDecimal > 0.5f) {
                centerY
            } else {
                centerY * 2 * tempDecimal
            }

            canvas.save()
            canvas.clipRect(0, 0, width, height / 2)
            if (mIsNegative) {
                negativeWidth = mColorPaint.measureText(negativeStr)
                canvas.drawText(negativeStr, 0f, centerY, mWhitePaint)
            }
            canvas.drawText(tempStr, negativeWidth+ dp2px(2f), textY, mWhitePaint)
            canvas.drawText("℃", negativeWidth+measureText + dp2px(4f), centerY, mWhitePaint)
            canvas.restore()

            canvas.save()
            canvas.clipRect(0, height / 2, width, height)
            if (mIsNegative) {
                canvas.drawText(negativeStr, 0f, centerY, mColorPaint)
            }
            canvas.drawText(tempStr, negativeWidth+ dp2px(2f), textY, mColorPaint)
            canvas.drawText("℃", negativeWidth+measureText + dp2px(4f), centerY, mColorPaint)
            canvas.restore()
        }
    }

    private var mDensity: Float = 0f//屏幕密度
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