package com.zeroone.marati.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zeroone.marati.R
import com.zeroone.marati.databinding.ActivityLoginBinding
import com.zeroone.marati.Home.HomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.button.setOnClickListener{
            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
        }
    }
}