package com.zeroone.marati.Home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zeroone.marati.core.data.source.remote.response.DashboardResponse
import com.zeroone.marati.core.data.source.remote.response.DataItem
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
import kotlin.math.log

class HomeViewModel(val pref:PreferenceManager):ViewModel() {

    private val _dashboards : MutableLiveData<List<DataItem>?> = MutableLiveData()
    val dashboards : LiveData<List<DataItem>?> = _dashboards

    private val _errorMessage : MutableLiveData<String> = MutableLiveData()
    val errorMessage : LiveData<String> = _errorMessage


    fun getToken():String{
        return runBlocking {
            pref.getToken().first()
        }
    }

    init {
        getDashboards()
    }
    fun getDashboards(){
        val token =getToken()
        if (token.isNotEmpty()){
            val header = mutableMapOf<String,String>()
            header["authorization"] = token
            val client = ApiConfig.provideApiServiceJs().getAllDashboard(header)
            client.enqueue(object : Callback<DashboardResponse>{
                override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
                    try {
                        if(response.isSuccessful){
                            _dashboards.value = response.body()?.data as List<DataItem>?
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
                    _errorMessage.value = t.message.toString()

                }

            })
        }
        else{
            _errorMessage.value = "unauthorized"
        }
    }

    fun deleteDashboard(uid:String){
        val token =getToken()
        if (token.isNotEmpty()){
            val header = mutableMapOf<String,String>()
            header["authorization"] = token

            val client = ApiConfig.provideApiServiceJs().deleteDashboard(header,uid)
            client.enqueue(object : Callback<DashboardResponse>{
                override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
                    try {
                        if(response.isSuccessful){
                            _dashboards.value = response.body()?.data as List<DataItem>?
                        } else{
                            val error = Gson().fromJson(response.errorBody()?.string(),DashboardResponse::class.java)
                            _errorMessage.value =  error.message!!
                        }
                    }catch (e:Exception){
                        _errorMessage.value = e.message.toString()
                    }
                }

                override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                    _errorMessage.value = t.message.toString()

                }

            })
        }

        else{
            _errorMessage.value = "unauthorized"
        }
    }
}