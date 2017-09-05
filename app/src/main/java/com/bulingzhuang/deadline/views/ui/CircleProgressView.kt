package com.bulingzhuang.deadline.views.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.bulingzhuang.deadline.R

/**
 * Created by bulingzhuang
 * on 2017/8/22
 * E-mail:bulingzhuang@foxmail.com
 */
class CircleProgressView : View {

    //    private var fillNum: Int = 0//总数值
//    private var currentNum: Int = 0//当前数值
    private var sweepAngle: Float = 0f//圆环划过的角度
//    private var lastAngle: Float = 0f//开始改变前划过角度
    private var mCircleColorS: Int? = 0//起始环形颜色
    private var mCircleColorE: Int? = 0//结束环形颜色
    private var mDensity: Float = 0f//屏幕密度
    private var mCircleRatio: Float = 0f//圆环宽度占比

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttr(context, attrs)
        init(context)
    }

    fun setData(currentNum: Int, fillNum: Int) {
        val futureSweepAngle = currentNum * 360f / fillNum
        val duration = futureSweepAngle / 360 * 2333 + 233f
//        lastAngle = sweepAngle
//        val anim = ObjectAnimator.ofFloat(this, "sweepAngle", futureSweepAngle - sweepAngle)
        val anim = ObjectAnimator.ofFloat(this, "sweepAngle", futureSweepAngle)
        anim.duration = Math.min(duration, 2000f).toLong()
        anim.interpolator = AccelerateDecelerateInterpolator()
        anim.start()
    }
    fun setDataNoAnim(currentNum: Int, fillNum: Int) {
        val futureSweepAngle = currentNum * 360f / fillNum
        sweepAngle = futureSweepAngle
    }

    fun setSweepAngle(angleDifference: Float) {
//        this.sweepAngle = angleDifference + lastAngle
        this.sweepAngle = angleDifference
        invalidate()
    }

    private val mCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mCircleBGPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mCirclePath = Path()
    private val mCircleBGPath = Path()

    /**
     * 静态
     */
    companion object {
        private val sCircleRatioTC = 6.6f
        private val sCircleRatioNTC = 4.4f
    }

    /**
     * 初始化
     */
    private fun init(context: Context) {
        mDensity = context.resources.displayMetrics.density
        mCirclePaint.strokeCap = Paint.Cap.ROUND
        mCirclePaint.style = Paint.Style.STROKE
        mCircleBGPaint.style = Paint.Style.STROKE
    }

    /**
     * 初始化参数
     */
    private fun initAttr(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView)
        val fillNum = typedArray.getInteger(R.styleable.CircleProgressView_fillNum, 100)
        val currentNum = typedArray.getInteger(R.styleable.CircleProgressView_currentNum, 0)
        sweepAngle = currentNum * 360f / fillNum
        mCircleColorS = typedArray.getColor(R.styleable.CircleProgressView_circleColorS, ContextCompat.getColor(context, R.color.colorPrimary))
        mCircleColorE = typedArray.getColor(R.styleable.CircleProgressView_circleColorE, ContextCompat.getColor(context, R.color.colorPrimary))
        val textCenter = typedArray.getBoolean(R.styleable.CircleProgressView_textCenter, false)
        mCircleRatio = if (textCenter) {
            sCircleRatioTC
        } else {
            sCircleRatioNTC
        }
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = setMeasureSize(widthMeasureSpec, 40f)
        val height = setMeasureSize(heightMeasureSpec, 40f)
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val minEdgeLen = Math.min(width, height)

        val shader = SweepGradient(minEdgeLen / 2f, minEdgeLen / 2f, mCircleColorS!!, mCircleColorE!!)
        mCirclePaint.shader = shader
//        mCirclePaint.color = ContextCompat.getColor(context,R.color.red500)
        mCircleBGPaint.color = Color.parseColor("#e9e9e9")

        mCircleBGPath.reset()
        mCircleBGPath.addCircle(w / 2f, h / 2f, minEdgeLen / 2f - minEdgeLen / mCircleRatio / 2f, Path.Direction.CW)

        mCirclePaint.strokeWidth = minEdgeLen / mCircleRatio
        mCircleBGPaint.strokeWidth = minEdgeLen / mCircleRatio
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val minEdgeLen = Math.min(width, height)
        setPath(minEdgeLen)
        if (canvas != null) {
            canvas.save()
            canvas.rotate(-105f, width / 2f, height / 2f)
            canvas.drawPath(mCircleBGPath, mCircleBGPaint)
            canvas.drawPath(mCirclePath, mCirclePaint)
            canvas.rotate(105f, width / 2f, height / 2f)
            canvas.restore()
        }
    }

    /**
     * 设置Path
     */
    private fun setPath(minEdgeLen: Int) {
        mCirclePath.reset()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCirclePath.addArc(Math.abs(width - minEdgeLen) / 2f + minEdgeLen / mCircleRatio / 2f, Math.abs(height - minEdgeLen) / 2f + minEdgeLen / mCircleRatio / 2f,
                    width - Math.abs(width - minEdgeLen) / 2f - minEdgeLen / mCircleRatio / 2f, height - Math.abs(height - minEdgeLen) / 2f - minEdgeLen / mCircleRatio / 2f,
                    18f, sweepAngle)
        } else {
            val circleRectF = RectF(Math.abs(width - minEdgeLen) / 2f + minEdgeLen / mCircleRatio / 2f, Math.abs(height - minEdgeLen) / 2f + minEdgeLen / mCircleRatio / 2f,
                    width - Math.abs(width - minEdgeLen) / 2f - minEdgeLen / mCircleRatio / 2f, height - Math.abs(height - minEdgeLen) / 2f - minEdgeLen / mCircleRatio / 2f)
            mCirclePath.addArc(circleRectF, 18f, sweepAngle)
        }
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