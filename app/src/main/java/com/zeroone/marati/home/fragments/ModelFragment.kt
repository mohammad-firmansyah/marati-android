package com.zeroone.marati.home.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.zeroone.marati.core.data.source.remote.response.ModelItem
import com.zeroone.marati.core.ui.DashboardAdapter
import com.zeroone.marati.core.ui.ModelAdapter
import com.zeroone.marati.databinding.FragmentModelBinding
import com.zeroone.marati.home.HomeActivity
import com.zeroone.marati.login.LoginActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ModelFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ModelFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentModelBinding? = null

    private val binding get() = _binding!!
    private var models : List<ModelItem>? = null
    private lateinit var parent : HomeActivity
    private lateinit var adapter : ModelAdapter


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
        // Inflate the layout for this fragment
        _binding = FragmentModelBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parent = requireActivity() as HomeActivity

        binding.shimmerProject.visibility = View.VISIBLE
        binding.shimmerProject.startShimmer()
        adapter = ModelAdapter(requireContext(),models,parent.viewModel)
        binding.rvModel.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvModel.adapter = ModelAdapter(requireContext(),models,parent.viewModel)

        binding.addModel.setOnClickListener {
            parent.addFragment(AddNewModelFragment())
        }
        binding.container.setOnRefreshListener {
            binding.rvModel.adapter = DashboardAdapter(requireContext(),null, parent.viewModel)
            binding.shimmerProject.visibility = View.VISIBLE
            binding.shimmerProject.startLayoutAnimation()

            parent.viewModel.getModels()

            binding.container.isRefreshing = false
        }

        binding.category.setOnCheckedStateChangeListener { group, checkedIds ->
            checkedIds.forEach{checkId ->
                val chip = group.findViewById<Chip>(checkId)

                if(chip.text == "All"){
                    parent.viewModel.getModels()
                } else if (chip.text == "My Model"){
                    parent.viewModel.getModelByUser()
                } else {
                    parent.viewModel.getModelByCategory(chip.text.toString())
                }
            }
        }

        viewModelListener()
    }

    private fun viewModelListener() {

        parent.viewModel.model.observe(viewLifecycleOwner){
            models = it
            binding.rvModel.adapter = ModelAdapter(requireContext(),models,parent.viewModel)
        }

        parent.viewModel.isLoading.observe(viewLifecycleOwner){
            binding.shimmerProject.stopShimmer()
            binding.shimmerProject.visibility = View.GONE
        }

        parent.viewModel.errorMessage.observe(viewLifecycleOwner){
            if(it == "unauthorized"){
                startActivity(
                    Intent(requireActivity(), LoginActivity::class.java)
                    .addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK))
            }else{
                Snackbar.make(parent.binding.root,it, Snackbar.LENGTH_LONG).show()
            }
        }
    }


    private fun addNewInput() {

    }

    private fun addNewModel() {


    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ModelFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ModelFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}