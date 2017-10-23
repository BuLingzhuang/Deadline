package com.bulingzhuang.deadline.views.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.bulingzhuang.deadline.R
import com.bulingzhuang.deadline.utils.showLogE

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
    private var mIconId = 0 //中心图标id

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttr(context, attrs)
        init(context)
    }

    fun setData(currentNum: Int, fillNum: Int) {
        if (currentNum > 0) {
            val futureSweepAngle = currentNum * 333f / fillNum
            val duration = futureSweepAngle / 360 * 2333 + 233f
//        lastAngle = sweepAngle
//        val anim = ObjectAnimator.ofFloat(this, "sweepAngle", futureSweepAngle - sweepAngle)
            val anim = ObjectAnimator.ofFloat(this, "sweepAngle", futureSweepAngle)
            if (duration > 0) {
                anim.duration = Math.min(duration, 2000f).toLong()
            } else {
                anim.duration = 2000L
            }
            anim.interpolator = AccelerateDecelerateInterpolator()
            anim.start()
        } else {
            setSweepAngle(0f)
        }
    }

    fun setDataNoAnim(currentNum: Int, fillNum: Int) {
        val futureSweepAngle = currentNum * 360f / fillNum
        sweepAngle = futureSweepAngle
    }

    /**
     * 动画使用的，不可private
     */
    fun setSweepAngle(sweepAngle: Float) {
//        this.sweepAngle = sweepAngle + lastAngle
        this.sweepAngle = sweepAngle
        invalidate()
    }

    private val mCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mIconPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mCircleBGPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mCirclePath = Path()
    private val mCircleBGPath = Path()
    private lateinit var mBitmapShader: BitmapShader

    /**
     * 静态
     */
    companion object {
        private val sCircleRatioTC = 6.6f
        private val sCircleRatioNTC = 4.4f
        private val sIconRatio = 0.6f //中间图标占比
        private val sBgGradualRotate = 105f //渐变背景旋转角度
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
     * 传入颜色（纯色）
     */
    fun setColor(color: String) {
        val parseColor = Color.parseColor(color)
        mCircleColorS = parseColor
        mCircleColorE = parseColor
        invalidate()
    }

    /**
     * 传入颜色（渐变）
     */
    fun setColor(startColor: String, endColor: String) {
        mCircleColorS = Color.parseColor(startColor)
        mCircleColorE = Color.parseColor(endColor)
        refreshShader()
        invalidate()
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
        val iconId = typedArray.getResourceId(R.styleable.CircleProgressView_icon, R.mipmap.ic_launcher_foreground)
        if (iconId != R.mipmap.ic_launcher_foreground) {
            mIconId = iconId
        }

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

    /**
     * 刷新shader
     */
    private fun refreshShader() {
        val minEdgeLen = Math.min(width, height)
        val shader = SweepGradient(minEdgeLen / 2f, minEdgeLen / 2f, mCircleColorS!!, mCircleColorE!!)
        mCirclePaint.shader = shader

        if (mIconId != 0) {
            val composeShader = ComposeShader(shader, mBitmapShader, PorterDuff.Mode.DST_IN)
            mIconPaint.shader = composeShader
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val minEdgeLen = Math.min(width, height)

        mCircleBGPaint.color = Color.parseColor("#e9e9e9")

        if (mIconId != 0) {
            val realBitmap = BitmapFactory.decodeResource(resources, mIconId)
            val matrix = Matrix()
            val maxLength = Math.max(realBitmap.width, realBitmap.height)
            val realLength = (Math.cos(2 * Math.PI / 360 * 15) + Math.sin(2 * Math.PI / 360 * 15)) * maxLength
            val min = Math.min(width.toFloat() / realLength, height.toFloat() / realLength).toFloat() * sIconRatio
//            showLogE("图片实际尺寸：w=${realBitmap.width}，h=${realBitmap.height}")
//            showLogE("min=$min，处理后长度：${min * realBitmap.width}")
            matrix.postScale(min, min)
            matrix.postRotate(sBgGradualRotate)
            val bitmap = Bitmap.createBitmap(realBitmap, 0, 0, realBitmap.width, realBitmap.height, matrix, true)
//            showLogE("控件的宽高：w=$w，h=$h；icon的宽高：w=${bitmap.width}，h=${bitmap.height}")
            mBitmapShader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        }

        refreshShader()

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
            canvas.rotate(-sBgGradualRotate, width / 2f, height / 2f)
            canvas.drawPath(mCircleBGPath, mCircleBGPaint)
            canvas.drawPath(mCirclePath, mCirclePaint)
            if (mIconId != 0) {
                canvas.translate(width.toFloat() * (1 - sIconRatio) / 2, height.toFloat() * (1 - sIconRatio) / 2)
                canvas.drawRect(0f, 0f, width.toFloat() * sIconRatio, height.toFloat() * sIconRatio, mIconPaint)
                canvas.translate(-width.toFloat() * (1 - sIconRatio) / 2, -height.toFloat() * (1 - sIconRatio) / 2)
            }
            canvas.rotate(sBgGradualRotate, width / 2f, height / 2f)
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