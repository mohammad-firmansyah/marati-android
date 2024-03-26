package com.zeroone.marati.home.fragments

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.zeroone.marati.R
import com.zeroone.marati.core.data.source.remote.response.Input
import com.zeroone.marati.core.data.source.remote.response.ModelItem
import com.zeroone.marati.core.data.source.remote.response.Output
import com.zeroone.marati.databinding.FragmentEditModelBinding
import com.zeroone.marati.home.HomeActivity

import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditModelFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditModelFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var name: String? = null
    private var description: String? = null
    private var category: String? = null
    private var filename: String? = null
    private var input: Input? = null
    private var output: Output? = null
    private var id: String? = null

    private var _binding : FragmentEditModelBinding? = null
    private val binding get() = _binding!!
    private var viewCounter = 0
    private lateinit var file : File
    private lateinit var activeModel : ModelItem
    private lateinit var parent : HomeActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString("name")
            description = it.getString("description")
            category = it.getString("category")
            filename = it.getString("file")
            input = it.getParcelable("input")
            output = it.getParcelable("output")
            id = it.getString("id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditModelBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parent = requireActivity() as HomeActivity

        getFilePermission()

        setInitialData()


        binding.addInput.setOnClickListener {
            val newView = layoutInflater.inflate(R.layout.input_item,null)
            newView.id = generateViewId()
            binding.inputWrapper.addView(newView)
        }

        binding.addOutput.setOnClickListener {
            val newView = layoutInflater.inflate(R.layout.input_item,null)
            newView.id = generateViewId()
            binding.outputWrapper.addView(newView)
        }

        binding.filebtnmodel.setOnClickListener {
            pickFile()
        }

        binding.addNewModel.setOnClickListener {
            try {
                val input = getDataFromViews(binding.inputWrapper)
                val output = getDataFromViews(binding.outputWrapper)
                Log.d("input",input["name"].toString())
                val model = ModelItem()
                model.name = binding.name.text.toString()
                model.description = binding.description.text.toString()
                model.category = binding.category.selectedItem.toString()
                val inputItem = Input()
                model.input = inputItem

                val outputItem = Output()
                model.output = outputItem

                parent.viewModel.addModel(model,file)
                parent.deleteFragment(this)
            }catch (e:Exception){
                e.printStackTrace()
            }

        }

        binding.toolbar.setNavigationOnClickListener{
            parent.deleteFragment(this)
        }
    }

    private fun setInitialData(){

        // set category
        val typeArray = parent.resources.getStringArray(R.array.category)
        typeArray.forEachIndexed {i,e ->
            if (e == category){
                binding.category.setSelection(i)
            }
        }

        binding.name.setText(name)
        binding.description.setText(description)
        binding.textView13.text = filename

//        val obj = Gson().fromJson(input.toString(),Input::class.java)
        addInputN(input?.name?.size!!-1)
        addOutputN(output?.name?.size!!-1)
//
    }


    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*" // You can set specific MIME types here, for example, "image/*" for images
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        startActivityForResult(intent, AddNewModelFragment.REQUEST_CODE)
    }

    private fun getFilePermission(){
        // Request storage permission
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE), 101)

    }
    private fun generateViewId(): Int {
        viewCounter++
        return View.generateViewId() + viewCounter
    }


    private fun getDataFromViews(linearLayout: LinearLayout) : HashMap<String,List<String>> {
        val name = mutableListOf<String>()
        val type = mutableListOf<String>()

        val map = HashMap<String,List<String>>()

        for (i in 0 until linearLayout.childCount) {
            val childView = linearLayout.getChildAt(i)


            if (childView is LinearLayout) {
                val spinner = findSpinner(childView)
                val spinnerSelectedItem = spinner?.selectedItem?.toString() ?: ""

                val editText = findEditText(childView)
                val editTextValue = editText?.text.toString()

                if (!editTextValue.isNullOrEmpty()){
                    if(!editTextValue.equals("null")){
                        name.add(editTextValue)
                        type.add(spinnerSelectedItem)
                    }
                }
            }

            map["name"] = name
            map["type"] = type

        }


        return map
    }

    private fun findSpinner(linearLayout: LinearLayout): Spinner? {
        for (i in 0 until linearLayout.childCount) {
            val view = linearLayout.getChildAt(i)
            if (view is Spinner) {
                return view
            }
        }
        return null
    }

    private fun findEditText(linearLayout: LinearLayout): EditText? {
        for (i in 0 until linearLayout.childCount) {
            val view = linearLayout.getChildAt(i)
            if (view is EditText) {
                return view
            }
        }
        return null
    }

    private fun findImageButton(linearLayout: LinearLayout): ImageButton? {
        for (i in 0 until linearLayout.childCount) {
            val view = linearLayout.getChildAt(i)
            if (view is ImageButton) {
                return view
            }
        }
        return null
    }

    private fun addInputN(n:Int){
        for (i in 0..n){
            // create linear layout
            val newView = layoutInflater.inflate(R.layout.input_item,null)
            newView.id = generateViewId()
            binding.inputWrapper.addView(newView)
            // find spinner and edit text
            val spinner = findSpinner(newView as LinearLayout)
            val editText = findEditText(newView as LinearLayout)
            val imageBtn = findImageButton(newView as LinearLayout)
            editText?.setText(input?.name?.get(i))
            imageBtn?.setOnClickListener {
                deleteChild(binding.inputWrapper,newView)
            }
            // loop over typed array to get selected item id
            val typeArray = parent.resources.getStringArray(R.array.type)
            typeArray.forEachIndexed {index,e ->
                if(input?.type?.get(i) == e){
                    spinner?.setSelection(index)
                }
            }

        }
    }
    private fun deleteChild(linearLayout: LinearLayout,child : View){
        linearLayout.removeView(child)
    }
    private fun addOutputN(n:Int){
        for (i in 0..n){
            // create linear layout
            val newView = layoutInflater.inflate(R.layout.input_item,null)
            newView.id = generateViewId()
            binding.outputWrapper.addView(newView)
            // find spinner and edit text
            val spinner = findSpinner(newView as LinearLayout)
            val editText = findEditText(newView as LinearLayout)
            editText?.setText(output?.name?.get(i))
            // loop over typed array to get selected item id
            val typeArray = parent.resources.getStringArray(R.array.type)
            typeArray.forEachIndexed {index,e ->
                if(output?.type?.get(i) == e){
                    spinner?.setSelection(index)
                }
            }
        }

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditModelFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(id:String?,name: String?,owner_id:String?, description: String?,category:String?,file:String?,input:Input?,output:Output?) =
            EditModelFragment().apply {
                arguments = Bundle().apply {
                    putString("name", name)
                    putString("description", description)
                    putString("owner_id", owner_id)
                    putString("category", category)
                    putString("file", file)
                    putParcelable("input", input)
                    putParcelable("output", output)
                    putString("id", id)
                }
            }
    }
}