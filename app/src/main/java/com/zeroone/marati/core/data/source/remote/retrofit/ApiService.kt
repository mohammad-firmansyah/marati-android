package com.zeroone.marati.core.data.source.remote.retrofit

import com.zeroone.marati.core.data.source.remote.response.LoginResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("/user/login/")
    fun login(@Body() body : RequestBody) : Call<LoginResponse>

    @POST("/user/register/")
    fun register() : Call<LoginResponse>

    @POST("/user/social-login/")
    fun socialLogin(@Body() body:RequestBody) : Call<LoginResponse>

    @GET("/dashboard/")
    fun getAllDashboard() : Call<LoginResponse>

}