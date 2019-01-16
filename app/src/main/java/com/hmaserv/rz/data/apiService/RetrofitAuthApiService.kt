package com.hmaserv.rz.data.apiService

import com.hmaserv.rz.domain.ApiResponse
import com.hmaserv.rz.domain.CreateProductRequest
import com.hmaserv.rz.domain.CreateProductResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface RetrofitAuthApiService {

    @PUT("auth/activeUser")
    fun verifyPhone(@Header("Authorization") token: String): Deferred<ApiResponse<Boolean>>

    @POST("ads/create")
    fun createProduct(
        @Header("Authorization") token: String,
        @Body createProductRequest: CreateProductRequest
    ) : Deferred<ApiResponse<CreateProductResponse>>

    @Multipart
    @POST("ads/uploader")
    fun upload(
        @Header("Authorization") token: String,
        @Part("ads_uuid") adUuid: RequestBody,
        @Part image: MultipartBody.Part
    ): Deferred<ApiResponse<String>>

    companion object {
        fun create(baseUrl: String, client: OkHttpClient): RetrofitAuthApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
                .client(client)
                .build()

            return retrofit.create(RetrofitAuthApiService::class.java)
        }
    }
}