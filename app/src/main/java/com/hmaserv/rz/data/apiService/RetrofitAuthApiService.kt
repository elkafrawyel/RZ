package com.hmaserv.rz.data.apiService

import com.hmaserv.rz.domain.*
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface RetrofitAuthApiService {

    @Multipart
    @POST("ads/uploader")
    fun upload(
        @Header("Authorization") token: String,
        @Part("ads_uuid") adUuid: RequestBody,
        @Part image: MultipartBody.Part
    ): Deferred<ApiResponse<String>>

    @PUT("auth/activeUser")
    fun verifyPhone(@Header("Authorization") token: String): Deferred<ApiResponse<Boolean>>

    @POST("ads/create")
    fun createProduct(
        @Header("Authorization") token: String,
        @Body createProductRequest: CreateProductRequest
    ): Deferred<ApiResponse<CreateProductResponse>>

    @GET("user/ads")
    fun myAds(@Header("Authorization") token: String): Deferred<ApiResponse<List<MiniAdResponse>>>

    @HTTP(method = "DELETE", path = "ads/delete", hasBody = true)
    fun deleteAd(
        @Header("Authorization") token: String,
        @Body request: AdRequest
    ): Deferred<ApiResponse<Boolean>>

    @PUT("ads/update")
    fun updateAd(
        @Header("Authorization") token: String,
        @Body request: CreateProductRequest
    ): Deferred<ApiResponse<CreateProductResponse>>

    @POST("order/create")
    fun createOrder(
        @Header("Authorization") token: String,
        @Body request: CreateOrderRequest
    ): Deferred<ApiResponse<Boolean>>

    @GET("user/orders")
    fun myOrders(
        @Header("Authorization") token: String
    ): Deferred<ApiResponse<List<MiniOrderResponse>>>

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