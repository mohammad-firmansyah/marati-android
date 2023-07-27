package com.zeroone.marati.ui.Onboarding

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.zeroone.marati.R
import com.zeroone.marati.databinding.ActivityOnboardingBinding
import com.zeroone.marati.ui.Login.LoginActivity
import com.zeroone.marati.ui.Register.RegisterActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        playAnimation()



        binding.loginBtn.setOnClickListener {
            startActivity(Intent(this@OnboardingActivity,LoginActivity::class.java))
        }

        binding.registerBtn.setOnClickListener {
            startActivity(Intent(this@OnboardingActivity,RegisterActivity::class.java))
        }

    }

    fun playAnimation(){
        ObjectAnimator.ofFloat(binding.textView, View.TRANSLATION_X, -60f, 0f).apply {
            duration = 600
            repeatCount = 0
        }.start()

        ObjectAnimator.ofFloat(binding.textView2, View.TRANSLATION_X, -60f, 0f).apply {
            duration = 800
            repeatCount = 0
        }.start()

        ObjectAnimator.ofFloat(binding.loginBtn, View.TRANSLATION_X, -60f, 0f).apply {
            duration = 1000
            repeatCount = 0
        }.start()

        ObjectAnimator.ofFloat(binding.registerBtn, View.TRANSLATION_X, -60f, 0f).apply {
            duration = 1200
            repeatCount = 0
        }.start()

    }
}