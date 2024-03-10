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
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditViewModel(val pref:PreferenceManager,val uid: String):ViewModel() {
    private val _components: MutableLiveData<List<ComponentItem>?> = MutableLiveData()
    val components : LiveData<List<ComponentItem>?> = _components

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
        getComponents(getToken(),uid)
    }

    fun getComponents(token:String,uid:String){

        val headers = HashMap<String,String>()
        headers.put("authorization",token)

        val client = ApiConfig.provideApiServiceJs().getComponents(headers,uid)
        client.enqueue(object: Callback<ComponentResponse>{
            override fun onResponse(
                call: Call<ComponentResponse>,
                response: Response<ComponentResponse>
            ) {
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
                _errorMessage.value = t.message.toString()
            }
        })
    }
}