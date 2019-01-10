package com.hmaserv.rz

import android.app.Application
import android.content.Context
import android.util.Log
import com.hmaserv.rz.domain.MyObjectBox
import com.hmaserv.rz.framework.apiService.RetrofitApiService
import com.hmaserv.rz.framework.apiService.RetrofitAuthApiService
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


class RzApplication : Application() {

    lateinit var boxStore: BoxStore
    lateinit var apiService: RetrofitApiService
    lateinit var authApiService: RetrofitAuthApiService

    override fun onCreate() {
        super.onCreate()
        instance = this
        boxStore = MyObjectBox.builder().androidContext(this).build()
        if (BuildConfig.DEBUG) {
            val started = AndroidObjectBrowser(boxStore).start(this)
            Log.i("ObjectBrowser", "Started: $started")
        }


        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC
        val acceptLanguage = Interceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("Accept-Language", "ar")
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(request)
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(acceptLanguage)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://159.65.45.31/api/v1/")
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
            .client(client)
            .build()

        apiService = retrofit.create(RetrofitApiService::class.java)
        authApiService = retrofit.create(RetrofitAuthApiService::class.java)
    }

    companion object {

        lateinit var instance: RzApplication
            private set

        fun getBoxStore(context: Context): BoxStore {
            return (context.applicationContext as RzApplication).boxStore
        }

        fun getApiService(context: Context): RetrofitApiService {
            return (context.applicationContext as RzApplication).apiService
        }

        fun getAuthApiService(context: Context): RetrofitAuthApiService {
            return (context.applicationContext as RzApplication).authApiService
        }
    }
}