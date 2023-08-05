package com.zeroone.marati.ui.Edit

import android.app.ActionBar
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.cardview.widget.CardView
import com.zeroone.marati.R
import com.zeroone.marati.databinding.ActivityEditBinding
import com.zeroone.marati.utils.BottomSheet
import com.zeroone.marati.utils.NavigationDrawer

class EditActivity : AppCompatActivity() {

    private lateinit var binding : ActivityEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.show.setOnClickListener {
            showNavDrawer()
        }
    }

    private fun showNavDrawer() {

        val infalter = this.layoutInflater
        val dialogView = infalter.inflate(R.layout.navigation_drawer,null)

        val isDialog = Dialog(this)


        isDialog.show()
        isDialog.window?.setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT)
        isDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isDialog.window?.getAttributes()?.windowAnimations = R.style.NavDialogAnimation
        isDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        isDialog.window?.setGravity(Gravity.END)
        isDialog.setContentView(dialogView)
        isDialog.setCancelable(true)

    }
}