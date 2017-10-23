package com.bulingzhuang.deadline.views.activitys

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bulingzhuang.deadline.R
import com.bulingzhuang.deadline.utils.showLogE
import com.bulingzhuang.deadline.utils.showSnakeBar
import com.bulingzhuang.deadline.views.fragments.ColorDialogFragment
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        init()
    }

    private fun init() {
        val set = AnimatorSet()
        val workAnim = ObjectAnimator.ofFloat(cpv_work, "alpha", 0f, 1f).setDuration(1000)
        val festivalAnim = ObjectAnimator.ofFloat(cpv_festival, "alpha", 0f, 1f).setDuration(1000)
        val birthdayAnim = ObjectAnimator.ofFloat(cpv_birthday, "alpha", 0f, 1f).setDuration(1000)
        val familyAnim = ObjectAnimator.ofFloat(cpv_family, "alpha", 0f, 1f).setDuration(1000)
        val otherAnim = ObjectAnimator.ofFloat(cpv_other, "alpha", 0f, 1f).setDuration(1000)

        val workRotation = ObjectAnimator.ofFloat(cpv_work, "rotationY", -180f, 0f).setDuration(1000)
        val festivalRotation = ObjectAnimator.ofFloat(cpv_festival, "rotationY", -180f, 0f).setDuration(1000)
        val birthdayRotation = ObjectAnimator.ofFloat(cpv_birthday, "rotationY", -180f, 0f).setDuration(1000)
        val familyRotation = ObjectAnimator.ofFloat(cpv_family, "rotationY", -180f, 0f).setDuration(1000)
        val otherRotation = ObjectAnimator.ofFloat(cpv_other, "rotationY", -180f, 0f).setDuration(1000)
        val delayDuration = 233L
        var tag = true
        otherRotation.addUpdateListener {
            val fl = it.animatedValue as Float
            showLogE("value=$fl")
            if (Math.abs(fl) < 90 && tag) {
                cpv_other.setColor("#37474f", "#37474f")
                tag = false
                showLogE("执行了")
            }
        }
        workAnim.startDelay = 0L
        workRotation.startDelay = 0L
        festivalAnim.startDelay = delayDuration
        festivalRotation.startDelay = delayDuration
        birthdayAnim.startDelay = delayDuration * 2
        birthdayRotation.startDelay = delayDuration * 2
        familyAnim.startDelay = 0
        familyRotation.startDelay = 0
        otherAnim.startDelay = delayDuration
        otherRotation.startDelay = delayDuration
        set.playTogether(workAnim, festivalAnim, birthdayAnim, familyAnim, otherAnim, workRotation, festivalRotation, birthdayRotation, familyRotation, otherRotation)

        rl_defaultColor.setOnClickListener {
            if (fl_defaultColor.visibility == View.VISIBLE) { //收起操作
                iv_defaultColor.animate().rotationX(0f)
                fl_defaultColor.visibility = View.GONE
                cpv_work.alpha = 0f
                cpv_festival.alpha = 0f
                cpv_birthday.alpha = 0f
                cpv_family.alpha = 0f
                cpv_other.alpha = 0f
            } else { //展示操作
                iv_defaultColor.animate().rotationX(180f)
                fl_defaultColor.visibility = View.VISIBLE
                set.start()
            }
        }

        setCpvListener(cpv_work, cpv_festival, cpv_birthday, cpv_family, cpv_other)
    }


    /**
     * 设置默认颜色的点击监听
     */
    private fun setCpvListener(vararg views: View) {
        val cpvListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.cpv_work -> {
                    showSnakeBar("work", ll_gen)
//                    ColorDialogFragment.newInstance(mIsGradient, mContentColor, mEndColor).show(supportFragmentManager, "colorDialog")
                }
                R.id.cpv_festival -> {
                    showSnakeBar("festival", ll_gen)
                }
                R.id.cpv_birthday -> {
                    showSnakeBar("birthday", ll_gen)
                }
                R.id.cpv_family -> {
                    showSnakeBar("family", ll_gen)
                }
                R.id.cpv_other -> {
                    showSnakeBar("other", ll_gen)
                }
            }
        }

        views.forEach { it.setOnClickListener(cpvListener) }
    }
}
