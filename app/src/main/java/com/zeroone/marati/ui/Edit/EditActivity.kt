package com.zeroone.marati.ui.Edit

import android.app.Dialog
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.ImageButton
import com.zeroone.marati.R
import com.zeroone.marati.custom.Circle
import com.zeroone.marati.custom.Switch
import com.zeroone.marati.databinding.ActivityEditDashboardBinding

class EditActivity : AppCompatActivity() {

    private lateinit var binding : ActivityEditDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.show.setOnClickListener {
            showNavDrawer()
        }

    }

    private fun showNavDrawer() {

        val infalter = this.layoutInflater
        val dialogView = infalter.inflate(R.layout.navigation_drawer,null)
        val isDialog = Dialog(this)

        val paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.FILL
        }

        val circle = Circle(300f, 400f, 100f, paint)
        val switch = Switch(this,300f,400f,200f,paint)

        dialogView.findViewById<ImageButton>(R.id.close).setOnClickListener {
            isDialog.dismiss()
        }

        dialogView.findViewById<ImageButton>(R.id.plus_siwtch).setOnClickListener {
//            binding.drawer.addObject(circle)
            binding.drawer.addObject(switch)
        }




        isDialog.show()
//        isDialog.window?.setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        isDialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        isDialog.window?.getAttributes()?.windowAnimations = R.style.NavDialogAnimation
        isDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        isDialog.window?.setGravity(Gravity.END)
        isDialog.setContentView(dialogView)
        isDialog.setCancelable(true)

    }
}