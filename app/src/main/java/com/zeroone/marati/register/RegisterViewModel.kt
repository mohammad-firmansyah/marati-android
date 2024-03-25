package com.zeroone.marati.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zeroone.marati.core.data.source.remote.response.LoginResponse
import com.zeroone.marati.core.data.source.remote.response.User
import com.zeroone.marati.core.data.source.remote.retrofit.ApiConfig
import com.zeroone.marati.core.ui.PreferenceManager
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val pref:PreferenceManager) : ViewModel(){
    val _user : MutableLiveData<User> = MutableLiveData<User>()
    val user : LiveData<User> = _user

    val _errorMessage : MutableLiveData<String> = MutableLiveData<String>()
    val errorMessage : LiveData<String> = _errorMessage

    fun setToken(token: String?){
        runBlocking {
            pref.setToken(token)
        }
    }

    fun setUserId(id: String?){
        runBlocking {
            pref.setUserId(id)
        }
    }

    fun register(email:String,password:String,name:String){
        val body = JSONObject()
        body.put("email",email)
        body.put("fullname",name)
        body.put("username","${name.split(" ").get(0)}")
        body.put("password",password)
        val requestBody = body.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val client = ApiConfig.provideApiServiceJs().register(requestBody)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                try {
                    if(response.isSuccessful){
                        _user.value = response.body()?.data!!
                    } else{
                        val errorBody = response.errorBody()?.string()
                        val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
                        val errorMessage = errorResponse.message
                        _errorMessage.value = errorMessage!!
                    }
                }catch (e:Exception){
                    _errorMessage.value = e.message
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _errorMessage.value = t.message.toString()
            }

        })
    }

    fun socialLogin(token:String){
        val body = JSONObject()
        body.put("token",token)
        val requestBody = body.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val client = ApiConfig.provideApiServiceJs().socialLogin(requestBody)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                try {
                    if(response.isSuccessful){
                        _user.value = response.body()?.data!!
                    } else{
                        _errorMessage.value = response.body()?.message!!
                    }
                }catch (e:Exception){
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
                    val errorMessage = errorResponse.message
                    _errorMessage.value = errorMessage!!
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _errorMessage.value = t.message.toString()
            }

        })
    }
}