package com.zeroone.marati.Register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zeroone.marati.R
import com.zeroone.marati.databinding.ActivityHomeBinding
import com.zeroone.marati.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}