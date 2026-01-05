package com.liuyuheng.a202304100228.myapplication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.liuyuheng.a202304100228.myapplication.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val SPLASH_DELAY: Long = 2500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.studentInfo.text = "学号：202304100228\n姓名：刘宇恒"

        startAnimations()

        Handler(Looper.getMainLooper()).postDelayed({
            navigateToMain()
        }, SPLASH_DELAY)
    }

    private fun startAnimations() {
        val logoScaleX = ObjectAnimator.ofFloat(binding.appLogo, View.SCALE_X, 0f, 1f)
        val logoScaleY = ObjectAnimator.ofFloat(binding.appLogo, View.SCALE_Y, 0f, 1f)
        val logoAlpha = ObjectAnimator.ofFloat(binding.appLogo, View.ALPHA, 0f, 1f)

        val appNameAlpha = ObjectAnimator.ofFloat(binding.appName, View.ALPHA, 0f, 1f)
        val appNameTranslateY = ObjectAnimator.ofFloat(binding.appName, View.TRANSLATION_Y, 50f, 0f)

        val studentInfoAlpha = ObjectAnimator.ofFloat(binding.studentInfo, View.ALPHA, 0f, 1f)
        val studentInfoTranslateY = ObjectAnimator.ofFloat(binding.studentInfo, View.TRANSLATION_Y, 50f, 0f)

        val logoAnimatorSet = AnimatorSet().apply {
            playTogether(logoScaleX, logoScaleY, logoAlpha)
            duration = 800
        }

        val appNameAnimatorSet = AnimatorSet().apply {
            playTogether(appNameAlpha, appNameTranslateY)
            duration = 600
            startDelay = 400
        }

        val studentInfoAnimatorSet = AnimatorSet().apply {
            playTogether(studentInfoAlpha, studentInfoTranslateY)
            duration = 600
            startDelay = 800
        }

        AnimatorSet().apply {
            playSequentially(logoAnimatorSet, appNameAnimatorSet, studentInfoAnimatorSet)
            start()
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}
