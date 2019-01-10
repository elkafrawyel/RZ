package com.hmaserv.rz.framework.apiService

import com.hmaserv.rz.data.apiService.IApiService
import com.hmaserv.rz.domain.*
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitApiService : IApiService {

    @POST("auth/login")
    fun login(@Body logInUserRequest: LogInUserRequest) : Deferred<ApiResponse<LoggedInUser>>

    @POST("auth/register")
    fun register(@Body registerUserRequest: RegisterUserRequest) : Deferred<ApiResponse<LoggedInUser>>

    @POST("auth/resetPassword")
    fun forgetPassword(@Body forgetPassword: ForgetPassword) : Deferred<ApiResponse<ForgetPassword>>
}