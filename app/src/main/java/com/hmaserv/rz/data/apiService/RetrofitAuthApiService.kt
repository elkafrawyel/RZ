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

    @PUT("auth/activeUser")
    fun verifyPhone(@Header("Authorization") token: String): Deferred<ApiResponse<Boolean>>

    @PUT("auth/upgradeRole")
    fun upgrade(
        @Header("Authorization") token: String,
        @Body request: UpgradeUserRequest
    ): Deferred<ApiResponse<Boolean>>

    @PUT("notification/putToken")
    fun sendFirebaseToken(
        @Header("Authorization") token: String,
        @Body request: FirebaseTokenRequest
    ): Deferred<ApiResponse<Boolean>>

    @Multipart
    @POST("ads/uploader")
    fun upload(
        @Header("Authorization") token: String,
        @Part("ads_uuid") adUuid: RequestBody,
        @Part image: MultipartBody.Part
    ): Deferred<ApiResponse<String>>

    @Multipart
    @POST("ads/uploader")
    fun deleteImage(
        @Header("Authorization") token: String,
        @Part("ads_uuid") adUuid: RequestBody,
        @Part("delete_file") imagePath: RequestBody,
        @Part("garbageCollector") flag: RequestBody
    ): Deferred<ApiResponse<String>>

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
        @Body request: UpdateAdRequest
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

    @POST("order/history")
    fun order(
        @Header("Authorization") token: String,
        @Body request: OrderRequest
    ): Deferred<ApiResponse<List<OrderResponse>>>

    @POST("order/action")
    fun orderAction(
        @Header("Authorization") token: String,
        @Body request: OrderActionRequest
    ): Deferred<ApiResponse<OrderActionResponse>>

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