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

    @GET("landing/slider")
    fun getSlider(): Deferred<ApiResponse<List<Slider>>>

    @GET("landing/promotions")
    fun getPromotions(): Deferred<ApiResponse<List<MiniAdResponse>>>

    @GET("categories")
    fun getCategories(): Deferred<ApiResponse<List<Category>>>

    @POST("category/subs")
    fun getSubCategories(@Body subCategoryRequest: SubCategoryRequest): Deferred<ApiResponse<List<SubCategory>>>

    @POST("subCategories/ads")
    fun getMiniAds(@Body miniAdRequest: MiniAdRequest): Deferred<ApiResponse<List<MiniAdResponse>>>

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