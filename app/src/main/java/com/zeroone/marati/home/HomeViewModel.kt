package com.zeroone.marati.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zeroone.marati.core.data.source.remote.response.DashboardItem
import com.zeroone.marati.core.data.source.remote.response.DashboardResponse
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

class HomeViewModel(val pref:PreferenceManager):ViewModel() {

    private val _dashboards : MutableLiveData<List<DashboardItem>?> = MutableLiveData()
    val dashboards : LiveData<List<DashboardItem>?> = _dashboards

    private val _errorMessage : MutableLiveData<String> = MutableLiveData()
    val errorMessage : LiveData<String> = _errorMessage

    fun getToken():String{
        return runBlocking {
            pref.getToken().first()
        }
    }

    init {
        getDashboards(getUserId())
    }

    fun getUserId() : String {
        return runBlocking {
            pref.getUserId().first()
        }
    }

    fun getDashboards(ownerId:String){
        val token =getToken()
        if (token.isNotEmpty()){
            val header = mutableMapOf<String,String>()
            header["authorization"] = token
            val client = ApiConfig.provideApiServiceJs().getAllDashboard(ownerId,header)
            client.enqueue(object : Callback<DashboardResponse>{
                override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
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
                    _errorMessage.value = t.message.toString()

                }

            })
        }
        else{
            _errorMessage.value = "unauthorized"
        }
    }

    fun deleteDashboard(uid:String,ownerId: String){
        val token =getToken()
        if (token.isNotEmpty()){
            val header = mutableMapOf<String,String>()
            header["authorization"] = token

            val client = ApiConfig.provideApiServiceJs().deleteDashboard(header,uid,ownerId)
            client.enqueue(object : Callback<DashboardResponse>{
                override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
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
                    _errorMessage.value = t.message.toString()

                }

            })
        }

        else{
            _errorMessage.value = "unauthorized"
        }
    }

    fun createDashboard(data : DashboardItem) {
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
                    _errorMessage.value = t.message.toString()
                }

            })
        } else{
            _errorMessage.value = "unauthorized"
        }
    }
}