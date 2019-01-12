package com.hmaserv.rz.data.apiService

import com.hmaserv.rz.domain.*
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RetrofitApiService {

    @POST("auth/login")
    fun login(@Body logInUserRequest: LogInUserRequest): Deferred<ApiResponse<LoggedInUser>>

    @POST("auth/register")
    fun register(@Body registerUserRequest: RegisterUserRequest): Deferred<ApiResponse<LoggedInUser>>

    @POST("auth/resetPassword")
    fun forgetPassword(@Body forgetPassword: ForgetPassword): Deferred<ApiResponse<Boolean>>

    @GET("categories")
    fun getCategories(): Deferred<ApiResponse<List<Category>>>

    @GET("subCategories")
    fun getSubCategories(): Deferred<ApiResponse<List<SubCategory>>>

    @POST("subCategories/ads")
    fun getProducts(@Body productRequest: ProductRequest): Deferred<ApiResponse<List<Product>>>

    companion object {
        fun create(baseUrl: String, client: OkHttpClient): RetrofitApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
                .client(client)
                .build()

            return retrofit.create(RetrofitApiService::class.java)
        }
    }
}