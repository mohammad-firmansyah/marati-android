package com.zeroone.marati.core.data.source.remote.retrofit

import com.zeroone.marati.core.data.source.remote.response.AddDashboardResponse
import com.zeroone.marati.core.data.source.remote.response.DashboardResponse
import com.zeroone.marati.core.data.source.remote.response.LoginResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("/user/login/")
    fun login(@Body() body : RequestBody) : Call<LoginResponse>

    @POST("/user/register/")
    fun register() : Call<LoginResponse>

    @POST("/user/social-login/")
    fun socialLogin(@Body() body:RequestBody) : Call<LoginResponse>

    @GET("/dashboard/")
    fun getAllDashboard(
        @HeaderMap headers: Map<String, String>

    ) : Call<DashboardResponse>

    @DELETE("/dashboard/{id}")
    fun deleteDashboard(
        @HeaderMap headers: Map<String, String>,
        @Path("id") id:String,
    ) : Call<DashboardResponse>

    @POST("/dashboard/")
    fun addDashboard(
        @HeaderMap headers: Map<String, String>,
        @Body() body: RequestBody,
    ) : Call<AddDashboardResponse>

}