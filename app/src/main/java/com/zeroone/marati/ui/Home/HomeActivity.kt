package com.zeroone.marati.ui.Home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zeroone.marati.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.addProject.setOnClickListener{
            showBottomSheet()
        }


    }

    private fun showBottomSheet() {
//        val dialog = Dialog(this)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.bottom_sheet)
//
//        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
//        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
//        dialog.window?.setGravity(Gravity.BOTTOM)
//        dialog.show()

        val bottomSheetFragment = BottomSheet()
        bottomSheetFragment.show(supportFragmentManager,"BottomDialog")
    }
}