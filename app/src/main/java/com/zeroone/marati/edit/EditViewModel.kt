package com.zeroone.marati.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zeroone.marati.core.data.source.remote.response.ComponentItem
import com.zeroone.marati.core.data.source.remote.response.ComponentResponse
import com.zeroone.marati.core.data.source.remote.retrofit.ApiConfig
import com.zeroone.marati.core.ui.PreferenceManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditViewModel(val pref:PreferenceManager,val uid: String):ViewModel() {
    private val _components: MutableLiveData<List<ComponentItem>?> = MutableLiveData()
    val components : LiveData<List<ComponentItem>?> = _components

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage : LiveData<String> = _errorMessage

    private val _message: MutableLiveData<String> = MutableLiveData()
    val message : LiveData<String> = _message

    fun setMessage(msg:String){
        this._message.value = msg
    }

    fun getToken() : String{
        return runBlocking {
            pref.getToken().first()
        }
    }

    init {
        getComponents(uid)
    }

    fun getComponents(uid:String){
        val token = getToken()
        if(token.isNotEmpty()){
        _isLoading.value = true
        val headers = HashMap<String,String>()
        headers.put("authorization",token)

        val client = ApiConfig.provideApiServiceJs().getComponents(headers,uid)
        client.enqueue(object: Callback<ComponentResponse>{
            override fun onResponse(
                call: Call<ComponentResponse>,
                response: Response<ComponentResponse>
            ) {
                _isLoading.value = false
                try {

                    if(response.isSuccessful){
                        _components.value = response.body()?.data as List<ComponentItem>
                    }else{
                        val error = Gson().fromJson(response.errorBody()?.string(),ComponentResponse::class.java)
                        _errorMessage.value = error.message.toString()
                    }
                } catch (e:Exception){
                    _errorMessage.value = e.message.toString()
                }

            }

            override fun onFailure(call: Call<ComponentResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message.toString()
            }
        })
        }
        else{
            _errorMessage.value = "Unauthorized"
        }
    }
    fun addComponent(data:ComponentItem){
        val token = getToken()
        _isLoading.value = true
        if(token.isNotEmpty()){

        val headers = HashMap<String,String>()
        headers.put("authorization",token)

        val json = JSONObject()
        json.put("id",data.id)
        json.put("type",data.type)
        json.put("x",data.x)
        json.put("y",data.y)
        json.put("w",data.w)
        json.put("h",data.h)
        json.put("content",data.content)
        json.put("topic",data.topic)
        json.put("rules",data.rules)
        json.put("dashboard_id",data.dashboardId)
        val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val client = ApiConfig.provideApiServiceJs().addComponent(headers,body)
        client.enqueue(object: Callback<ComponentResponse>{
            override fun onResponse(
                call: Call<ComponentResponse>,
                response: Response<ComponentResponse>
            ) {
                try {

                    _isLoading.value = false
                    if(response.isSuccessful){
                        _components.value = response.body()?.data as List<ComponentItem>
                    }else{
                        val error = Gson().fromJson(response.errorBody()?.string(),ComponentResponse::class.java)
//                        _errorMessage.value = error.message.toString()
                    }
                } catch (e:Exception){
//                    _errorMessage.value = e.message.toString()
                }

            }

            override fun onFailure(call: Call<ComponentResponse>, t: Throwable) {
//                _isLoading.value = false
//                _errorMessage.value = t.message.toString()
            }
        })
        }else{
            _errorMessage.value = "Unauthorized"
        }
    }

    fun updateComponent(data:ComponentItem){
        try {
            val token = getToken()
            _isLoading.value = true
            if(token.isNotEmpty()){

                val headers = HashMap<String,String>()
                headers.put("authorization",token)

                val json = JSONObject()
                json.put("id",data.id)
                json.put("type",data.type)
                json.put("x",data.x)
                json.put("y",data.y)
                json.put("w",data.w)
                json.put("h",data.h)
                json.put("content",data.content)
                json.put("topic",data.topic)
                json.put("rules",data.rules)
                json.put("dashboard_id",uid)
                json.put("model_id",null)
                val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
                val client = data.id?.let {
                    ApiConfig.provideApiServiceJs().updateComponent(headers,body,
                        it
                    )
                }
                client!!.enqueue(object: Callback<ComponentResponse>{
                    override fun onResponse(
                        call: Call<ComponentResponse>,
                        response: Response<ComponentResponse>
                    ) {
                        try {

                            _isLoading.value = false
                            if(response.isSuccessful){
                                _components.value = response.body()?.data as List<ComponentItem>
                            }else{
                                val error = Gson().fromJson(response.errorBody()?.string(),ComponentResponse::class.java)
//                        _errorMessage.value = error.message.toString()
                            }
                        } catch (e:Exception){
//                    _errorMessage.value = e.message.toString()
                        }

                    }

                    override fun onFailure(call: Call<ComponentResponse>, t: Throwable) {
//                _isLoading.value = false
//                _errorMessage.value = t.message.toString()
                    }
                })

            }else{
                _errorMessage.value = "Unauthorized"
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }
}