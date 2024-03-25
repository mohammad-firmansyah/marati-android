package com.zeroone.marati.home.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.zeroone.marati.R
import com.zeroone.marati.core.data.source.remote.response.Input
import com.zeroone.marati.core.data.source.remote.response.ModelItem
import com.zeroone.marati.core.data.source.remote.response.Output
import com.zeroone.marati.databinding.FragmentAddNewModelBinding
import com.zeroone.marati.home.HomeActivity
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddNewModelFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddNewModelFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding : FragmentAddNewModelBinding? = null
    private val binding get()=_binding!!
    private lateinit var parent : HomeActivity
    private var viewCounter = 0
    private lateinit var file : File

    //    {"name":"[]","type":[]}
    var outputHashMap = JSONObject()
    var inputHashMap = JSONObject()

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*" // You can set specific MIME types here, for example, "image/*" for images
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                val contentResolver = requireActivity().contentResolver
                val inputStream = contentResolver.openInputStream(uri)

                val tempFile = File.createTempFile("temp",".sav")

                inputStream.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input?.copyTo(output)
                    }
                }
                file = tempFile

                binding.textView13.text = tempFile.name

            }
        }
    }


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
        _binding = FragmentAddNewModelBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parent = requireActivity() as HomeActivity

        getFilePermission()

        binding.toolbar.inflateMenu(R.menu.toolbar_menu)

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
                inputItem.name = input["name"].toString()
                inputItem.type = input["type"].toString()
                model.input = inputItem

                val outputItem = Output()
                outputItem.name = output["name"].toString()
                outputItem.type = output["type"].toString()
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


    private fun getFilePermission(){
        // Request storage permission
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE), 101)

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


    companion object {
        const val REQUEST_CODE = 50

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddNewModelFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}