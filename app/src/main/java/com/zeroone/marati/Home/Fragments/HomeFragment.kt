package com.zeroone.marati.Home.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.zeroone.marati.databinding.FragmentHomeBinding
import com.zeroone.marati.Home.BottomSheetAddNewProject
import com.zeroone.marati.Home.HomeActivity
import com.zeroone.marati.Login.LoginActivity
import com.zeroone.marati.core.data.source.remote.response.DataItem
import com.zeroone.marati.core.ui.DashboardAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    private var dashboards : List<DataItem>? = null
    private lateinit var parent : HomeActivity

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parent = requireActivity() as HomeActivity

        binding.rvDashboard.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvDashboard.adapter = DashboardAdapter(requireContext(),dashboards, parent.viewModel)
        binding.addProject.setOnClickListener {

            showBottomSheet()
        }

        viewModelListener()
    }

    private fun viewModelListener() {
        parent.viewModel.dashboards.observe(viewLifecycleOwner){
            dashboards = it
            binding.rvDashboard.adapter = DashboardAdapter(requireContext(),dashboards,parent.viewModel)
        }

        parent.viewModel.errorMessage.observe(viewLifecycleOwner){
            if(it == "unauthorized"){
                startActivity(Intent(requireActivity(),LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK))
            }else{
                Snackbar.make(parent.binding.root,it,Snackbar.LENGTH_LONG).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showBottomSheet() {
        val bottomSheetFragment = BottomSheetAddNewProject()
        bottomSheetFragment.show(parentFragmentManager,"BottomDialog")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}