package com.zeroone.marati.core.data.source.remote.retrofit

import com.zeroone.marati.core.data.source.remote.response.ComponentResponse
import com.zeroone.marati.core.data.source.remote.response.DashboardResponse
import com.zeroone.marati.core.data.source.remote.response.LoginResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("/user/login/")
    fun login(@Body() body : RequestBody) : Call<LoginResponse>

    @POST("/user/register/")
    fun register() : Call<LoginResponse>

    @POST("/user/social-login/")
    fun socialLogin(@Body() body:RequestBody) : Call<LoginResponse>

    @GET("/dashboard/{owner_id}")
    fun getAllDashboard(
        @Path("owner_id") owner_id:String,
        @HeaderMap headers: Map<String, String>

    ) : Call<DashboardResponse>

    @DELETE("/dashboard/{id}/{ownerId}")
    fun deleteDashboard(
        @HeaderMap headers: Map<String, String>,
        @Path("id") id:String,
        @Path("ownerId") ownerId: String,
    ) : Call<DashboardResponse>

    @POST("/dashboard/")
    fun addDashboard(
        @HeaderMap headers: Map<String, String>,
        @Body() body: RequestBody,
    ) : Call<DashboardResponse>

    @GET("/component/{id}/dashboard")
    fun getComponents(
        @HeaderMap headers: Map<String, String>,
        @Path("id") id : String
    ) : Call<ComponentResponse>

    @POST("/component/")
    fun addComponent(
        @HeaderMap headers: Map<String, String>,
        @Body() body : RequestBody
    ) : Call<ComponentResponse>

    @PATCH("/component/{id}")
    fun updateComponent(
        @HeaderMap headers: Map<String, String>,
        @Body() body : RequestBody,
        @Path("id") id : String
    ) : Call<ComponentResponse>

}