package com.zeroone.marati

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.zeroone.marati.ui.Onboarding.OnboardingActivity
import com.zeroone.marati.ui.Splashscreen.SplashscreenActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        supportActionBar?.hide()

        Handler().postDelayed({
            finish()
            startActivity(Intent(this@MainActivity, OnboardingActivity::class.java))
        }, 2000)

    }
}