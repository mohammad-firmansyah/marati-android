package com.zeroone.marati.utils

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zeroone.marati.R
import com.zeroone.marati.databinding.FragmentHomeBinding
import com.zeroone.marati.ui.Edit.EditActivity

class BottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet,container,false)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<CheckBox>(R.id.cred_button).setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                view.findViewById<EditText>(R.id.username).visibility = View.VISIBLE
                view.findViewById<EditText>(R.id.password).visibility = View.VISIBLE
            } else {
                view.findViewById<EditText>(R.id.username).visibility = View.GONE
                view.findViewById<EditText>(R.id.password).visibility = View.GONE
            }
        }

        view.findViewById<Button>(R.id.addNewProject).setOnClickListener {
            startActivity(Intent(view.context, EditActivity::class.java))
        }
    }
}
