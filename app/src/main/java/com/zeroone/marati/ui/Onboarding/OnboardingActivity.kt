package com.zeroone.marati.ui.Onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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


        binding.loginBtn.setOnClickListener {
            startActivity(Intent(this@OnboardingActivity,LoginActivity::class.java))
        }

        binding.registerBtn.setOnClickListener {
            startActivity(Intent(this@OnboardingActivity,RegisterActivity::class.java))
        }

    }
}