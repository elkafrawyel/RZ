package com.hmaserv.rz.data.apiService

import com.hmaserv.rz.domain.ApiResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Header
import retrofit2.http.PUT

interface RetrofitAuthApiService {

    @PUT("auth/activeUser")
    fun verifyPhone(@Header("Authorization") token: String): Deferred<ApiResponse<Boolean>>

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