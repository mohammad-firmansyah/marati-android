package com.zeroone.marati.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.zeroone.marati.core.data.source.remote.response.DashboardItem
import com.zeroone.marati.core.data.source.remote.response.DashboardResponse
import com.zeroone.marati.core.data.source.remote.response.Input
import com.zeroone.marati.core.data.source.remote.response.ModelItem
import com.zeroone.marati.core.data.source.remote.response.ModelResponse
import com.zeroone.marati.core.data.source.remote.retrofit.ApiConfig
import com.zeroone.marati.core.ui.PreferenceManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.Locale.Category

class HomeViewModel(val pref:PreferenceManager):ViewModel() {

    private val _dashboards : MutableLiveData<List<DashboardItem>?> = MutableLiveData()
    val dashboards : LiveData<List<DashboardItem>?> = _dashboards

    private val _model : MutableLiveData<List<ModelItem>?> = MutableLiveData()
    val model : LiveData<List<ModelItem>?> = _model

    private val _errorMessage : MutableLiveData<String> = MutableLiveData()
    val errorMessage : LiveData<String> = _errorMessage

    private val _isLoading : MutableLiveData<Boolean> = MutableLiveData()
    val isLoading : LiveData<Boolean> = _isLoading

    fun getToken():String{
        return runBlocking {
            pref.getToken().first()
        }
    }

    init {
        getDashboards(getUserId())
        getModels()
    }

    fun getUserId() : String {
        return runBlocking {
            pref.getUserId().first()
        }
    }

    fun getDashboards(ownerId:String){
        _isLoading.value = true
        val token =getToken()
        if (token.isNotEmpty()){
            val header = mutableMapOf<String,String>()
            header["authorization"] = token
            val client = ApiConfig.provideApiServiceJs().getAllDashboard(ownerId,header)
            client.enqueue(object : Callback<DashboardResponse>{
                override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
                    _isLoading.value = false
                    try {
                        if(response.isSuccessful){
                            _dashboards.value = response.body()?.data as List<DashboardItem>?
                        } else{
                            if(response.code() == 401){
                                _errorMessage.value = "unauthorized"
                            }else{
                                val error = Gson().fromJson(response.errorBody()?.string(),DashboardResponse::class.java)
                                _errorMessage.value =  error.message!!
                            }

                        }
                    }catch (e:Exception){
                        _errorMessage.value = e.message.toString()
                    }

                }

                override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                    _isLoading.value = false
                    _errorMessage.value = t.message.toString()
                }

            })
        }
        else{
            _isLoading.value = false
            _errorMessage.value = "unauthorized"
        }
    }

    fun deleteDashboard(uid:String,ownerId: String){
        _isLoading.value = true
        val token =getToken()
        if (token.isNotEmpty()){
            val header = mutableMapOf<String,String>()
            header["authorization"] = token

            val client = ApiConfig.provideApiServiceJs().deleteDashboard(header,uid,ownerId)
            client.enqueue(object : Callback<DashboardResponse>{
                override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
                    _isLoading.value = false
                    try {
                        if(response.isSuccessful){
                            _dashboards.value = response.body()?.data as List<DashboardItem>?
                        } else{
                            val error = Gson().fromJson(response.errorBody()?.string(),DashboardResponse::class.java)
                            _errorMessage.value =  error.message!!
                        }
                    }catch (e:Exception){
                        _errorMessage.value = e.message.toString()
                    }
                }

                override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                    _isLoading.value = false
                    _errorMessage.value = t.message.toString()

                }

            })
        }

        else{
            _isLoading.value = false
            _errorMessage.value = "unauthorized"
        }
    }

    fun createDashboard(data : DashboardItem) {
        _isLoading.value = false
        val token = getToken()
        if (token.isNotEmpty()) {
            val json = JSONObject()
            json.put("name", data.name)
            json.put("username", data.username)
            json.put("password", data.password)
            json.put("description", data.description)
            json.put("server", data.server)
            json.put("owner_id", data.ownerId)
            val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())

            val header = HashMap<String, String>()
            header["authorization"] = token
            val client = ApiConfig.provideApiServiceJs().addDashboard(header,body)
            client.enqueue(object : Callback<DashboardResponse>{
                override fun onResponse(
                    call: Call<DashboardResponse>,
                    response: Response<DashboardResponse>
                ) {
                    _isLoading.value = false
                    try {
                        if(response.isSuccessful){
                            _dashboards.value = response.body()?.data as List<DashboardItem>
                        } else{
                            val error = Gson().fromJson(response.errorBody()?.string(),DashboardResponse::class.java)
                            _errorMessage.value =  error.message!!
                        }
                    } catch (e:Exception){
                        _errorMessage.value = e.message.toString()
                    }
                }

                override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                    _isLoading.value = false
                    _errorMessage.value = t.message.toString()
                }

            })
        } else{
            _isLoading.value = false
            _errorMessage.value = "unauthorized"
        }
    }

    fun getModels(){
        _isLoading.value = true

        val headers : HashMap<String,String> = HashMap()
        headers.put("accept","application/json")
        val client = ApiConfig.provideApiServicePy().getModels(headers)
        client.enqueue(object : Callback<ModelResponse>{
            override fun onResponse(call: Call<ModelResponse>, response: Response<ModelResponse>) {
                try {
                    _isLoading.value = false
                    if(response.isSuccessful){
                        _model.value = response.body()?.data as List<ModelItem>?
                    }
                }catch (e:Exception){
                    _isLoading.value = false
                    _errorMessage.value = e.message.toString()
                }
            }

            override fun onFailure(call: Call<ModelResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message.toString()
            }

        })
    }
    fun getModelByUser(){
        _isLoading.value = true

        val headers : HashMap<String,String> = HashMap()
        headers.put("accept","application/json")
        val client = ApiConfig.provideApiServicePy().getModelByUser(headers,getUserId())
        client.enqueue(object : Callback<ModelResponse>{
            override fun onResponse(call: Call<ModelResponse>, response: Response<ModelResponse>) {
                try {
                    _isLoading.value = false
                    if(response.isSuccessful){
                        _model.value = response.body()?.data as List<ModelItem>?
                    }
                }catch (e:Exception){
                    _isLoading.value = false
                    _errorMessage.value = e.message.toString()
                }
            }

            override fun onFailure(call: Call<ModelResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message.toString()
            }

        })
    }

    fun getModelByCategory(category: String){
        _isLoading.value = true

        val headers : HashMap<String,String> = HashMap()
        headers.put("accept","application/json")
        val client = ApiConfig.provideApiServicePy().getModelByCategory(headers,category)
        client.enqueue(object : Callback<ModelResponse>{
            override fun onResponse(call: Call<ModelResponse>, response: Response<ModelResponse>) {
                try {
                    _isLoading.value = false
                    if(response.isSuccessful){
                        _model.value = response.body()?.data as List<ModelItem>?
                    }
                }catch (e:Exception){
                    _isLoading.value = false
                    _errorMessage.value = e.message.toString()
                }
            }

            override fun onFailure(call: Call<ModelResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message.toString()
            }

        })
    }

    fun addModel(model:ModelItem,file: File){
        _isLoading.value = true

        val headers : HashMap<String,String> = HashMap()
        headers.put("accept","application/json")

        val requestFile = file.asRequestBody("application/octet-stream".toMediaType())

        val inputJson = JSONObject()
        inputJson.put("name",model.input!!.name)
        inputJson.put("type",model.input!!.type)

        val outputJson = JSONObject()
        outputJson.put("name",model.output!!.name)
        outputJson.put("type",model.output!!.type)

        val multiPart = MultipartBody.Part.createFormData("file",file.name,requestFile)
        val client = ApiConfig.provideApiServicePy().addModel(model.name!!,inputJson.toString(),outputJson.toString(),getUserId(),model.description.toString(),model.category.toString(),multiPart)
        client.enqueue(object : Callback<ModelResponse>{
            override fun onResponse(call: Call<ModelResponse>, response: Response<ModelResponse>) {
                try {
                    _isLoading.value = false
                    if(response.isSuccessful){
                        _model.value = response.body()?.data as List<ModelItem>?
                    }
                }catch (e:Exception){
                    _isLoading.value = false
                    _errorMessage.value = e.message.toString()
                }
            }

            override fun onFailure(call: Call<ModelResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message.toString()
            }

        })
    }

    fun logout() {
        viewModelScope.launch {
            pref.clearDataStore()
        }
    }
}