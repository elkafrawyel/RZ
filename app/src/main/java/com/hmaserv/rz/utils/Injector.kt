package com.hmaserv.rz.utils

import android.util.Log
import com.facebook.spectrum.DefaultPlugins
import com.facebook.spectrum.Spectrum
import com.facebook.spectrum.logging.SpectrumLogcatLogger
import com.hmaserv.rz.RzApplication
import com.hmaserv.rz.data.apiService.RetrofitApiService
import com.hmaserv.rz.data.apiService.RetrofitAuthApiService
import com.hmaserv.rz.framework.categories.CategoriesRemoteSource
import com.hmaserv.rz.framework.categories.CategoriesRepo
import com.hmaserv.rz.framework.createProduct.CreateProductRemoteSource
import com.hmaserv.rz.framework.createProduct.CreateProductRepo
import com.hmaserv.rz.framework.home.HomeRemoteSource
import com.hmaserv.rz.framework.home.HomeRepo
import com.hmaserv.rz.framework.logedInUser.LoggedInUserLocalSource
import com.hmaserv.rz.framework.logedInUser.LoggedInUserRemoteSource
import com.hmaserv.rz.framework.logedInUser.LoggedInUserRepo
import com.hmaserv.rz.framework.ads.AdsRemoteSource
import com.hmaserv.rz.framework.ads.AdsRepo
import com.hmaserv.rz.framework.settings.SettingsLocalSource
import com.hmaserv.rz.framework.settings.SettingsRepo
import com.hmaserv.rz.framework.subCategories.SubCategoriesRemoteSource
import com.hmaserv.rz.framework.subCategories.SubCategoriesRepo
import com.hmaserv.rz.usecases.*
import com.hmaserv.rz.utils.Constants.BASE_URL
import com.hmaserv.rz.utils.Constants.Language
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object Injector {

    var language = Language.DEFAULT
        set(value) {
            LoggedInUserRepo.resetRemoteSource(getLoggedInRemoteSource())
        }

    private val mSpectrum : Spectrum = Spectrum.make(
        SpectrumLogcatLogger(Log.INFO),
        DefaultPlugins.get()
    )

    fun getApplicationContext() = RzApplication.instance
    fun getCoroutinesDispatcherProvider() = CoroutinesDispatcherProvider(
        Dispatchers.Main,
        Dispatchers.Default,
        Dispatchers.IO
    )
    private fun getLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    private fun getApiServiceHeader(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("Accept-Language", language.value)
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(request)
        }
    }
    private fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(getApiServiceHeader())
            .addInterceptor(getLoggingInterceptor())
            .build()
    }

    private fun getApiService() = RetrofitApiService.create(BASE_URL, getOkHttpClient())
    private fun getAuthApiService() = RetrofitAuthApiService.create(BASE_URL, getOkHttpClient())
    private fun getBoxStore() = getApplicationContext().getBoxStore()
    private fun getSpectrum() = mSpectrum

    // Settings
    private fun getSettingsLocalSource() = SettingsLocalSource(getBoxStore())
    private fun getSettingsRepo() = SettingsRepo(getSettingsLocalSource())
    fun getCurrentLanguageUseCase() = CurrentLanguageUseCase(getSettingsRepo())
    fun setChangeLanguageUseCase() = ChangeLanguageUseCase(getSettingsRepo())

    // LoggedIn repo
    private fun getLoggedInRemoteSource() = LoggedInUserRemoteSource(getApiService(), getAuthApiService())
    private fun getLoggedInLocalSource() = LoggedInUserLocalSource(getBoxStore())
    private fun getLoggedInRepo() = LoggedInUserRepo.getInstance(
        getLoggedInRemoteSource(),
        getLoggedInLocalSource()
    )

    // LoggedIn use cases
    fun getLoginUseCase() = LoginUserUseCase(getLoggedInRepo())
    fun getRegisterUseCase() = RegisterUserUseCase(getLoggedInRepo())
    fun getVerifyPhoneUseCase() = VerifyPhoneUseCase(getLoggedInRepo())
    fun getForgetPasswordUseCase() = ForgetPasswordUseCase(getLoggedInRepo())
    fun getLogOutUseCase() = LogOutUseCase(getLoggedInRepo())
    fun getLoggedInUserListenerUseCase() = LogInListenerUseCase(getLoggedInRepo())

    // Categories
    private fun getCategoriesRemoteSource() = CategoriesRemoteSource(getApiService())
    private fun getCategoriesRepo() = CategoriesRepo.getInstance(
        getCategoriesRemoteSource()
    )

    fun getCategoriesUseCase() = GetCategoriesUseCase(getCategoriesRepo())

    // SubCategories
    private fun getSubCategoriesRemoteSource() = SubCategoriesRemoteSource(getApiService())
    private fun getSubCategoriesRepo() = SubCategoriesRepo.getInstance(
        getSubCategoriesRemoteSource()
    )

    fun getSubCategoriesUseCase() = GetSubCategoriesUseCase(getSubCategoriesRepo())

    // Products
    private fun getAdsRemoteSource() = AdsRemoteSource(getApiService())
    private fun getAdsRepo() = AdsRepo.getInstance(
        getAdsRemoteSource()
    )

    fun getAdsUseCase() = GetMiniAdsUseCase(getAdsRepo())

    // home
    private fun getHomeRemoteSource() = HomeRemoteSource(getApiService())
    private fun getHomeRepo() = HomeRepo.getInstance(getHomeRemoteSource())
    fun getSliderUseCase() = GetSliderUseCase(getHomeRepo())
    fun getPromotionsUseCase() = GetPromotionsUseCase(getHomeRepo())

    // create product
    private fun getCreateProductRemoteSource() = CreateProductRemoteSource(getAuthApiService())
    private fun getCreateProductRepo() = CreateProductRepo.getInstance(getCreateProductRemoteSource())
    fun getCreateProductUseCase() = CreateProductUseCase(getLoggedInRepo(), getCreateProductRepo())

    // Ad Details
    fun getAdUseCase() = GetAdUseCase(getAdsRepo())
}