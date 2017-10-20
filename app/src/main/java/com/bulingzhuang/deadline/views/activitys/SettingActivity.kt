package com.bulingzhuang.deadline.views.activitys

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bulingzhuang.deadline.R
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
//        val lineAnim = ObjectAnimator.ofFloat(v_defaultColor, "alpha", 0f, 1f).setDuration(1000)
        val workRotation = ObjectAnimator.ofFloat(cpv_work, "rotationY", -180f, 0f).setDuration(1000)
        val festivalRotation = ObjectAnimator.ofFloat(cpv_festival, "rotationY", -180f, 0f).setDuration(1000)
        val birthdayRotation = ObjectAnimator.ofFloat(cpv_birthday, "rotationY", -180f, 0f).setDuration(1000)
        val familyRotation = ObjectAnimator.ofFloat(cpv_family, "rotationY", -180f, 0f).setDuration(1000)
        val otherRotation = ObjectAnimator.ofFloat(cpv_other, "rotationY", -180f, 0f).setDuration(1000)
        val delayDuration = 233L
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
//        lineAnim.startDelay = delayDuration * 2
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
//                v_defaultColor.alpha = 0f
                fl_defaultColor.visibility = View.VISIBLE
//                cpv_work.animate().alpha(1f)
//                cpv_festival.animate().alpha(1f)
//                cpv_birthday.animate().alpha(1f)
//                cpv_family.animate().alpha(1f)
//                cpv_other.animate().alpha(1f)
                set.start()
            }
        }

    }
}
