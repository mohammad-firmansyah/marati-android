package com.zeroone.marati.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zeroone.marati.R
import com.zeroone.marati.core.data.source.remote.response.DashboardItem
import com.zeroone.marati.databinding.BottomSheetBinding

class BottomSheetAddNewProject : BottomSheetDialogFragment() {

    private lateinit var parent : HomeActivity
    private var _binding : BottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetBinding.inflate(inflater, container, false)
        return binding.root
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


        parent = requireActivity() as HomeActivity

        binding.credButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.username.visibility = View.VISIBLE
                binding.password.visibility = View.VISIBLE
            } else {
                binding.username.visibility = View.GONE
                binding.password.visibility = View.GONE
            }
        }

        view.findViewById<Button>(R.id.addNewProject).setOnClickListener {
            val data = DashboardItem(
                binding.server.text.toString(),
                binding.password.text.toString(),
                parent.viewModel.getUserId(),
                binding.name.text.toString(),
                binding.description.text.toString(),
                null,
                null,
                binding.username.text.toString(),
            )

            parent.viewModel.createDashboard(data)
            this.dismiss()

        }

    }


}
