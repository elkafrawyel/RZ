package com.hmaserv.rz.utils

import com.hmaserv.rz.RzApplication
import com.hmaserv.rz.data.apiService.RetrofitApiService
import com.hmaserv.rz.data.apiService.RetrofitAuthApiService
import com.hmaserv.rz.framework.categories.CategoriesRemoteSource
import com.hmaserv.rz.framework.categories.CategoriesRepo
import com.hmaserv.rz.framework.logedInUser.LoggedInUserLocalSource
import com.hmaserv.rz.framework.logedInUser.LoggedInUserRemoteSource
import com.hmaserv.rz.framework.logedInUser.LoggedInUserRepo
import com.hmaserv.rz.framework.products.ProductsRemoteSource
import com.hmaserv.rz.framework.products.ProductsRepo
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
    fun getForgetPasswordUseCase() = ForgetPasswordUseCase(getLoggedInRepo())

    // Categories
    private fun getCategoriesRemoteSource() = CategoriesRemoteSource(getApiService())
    private fun getCategoriesRepo() = CategoriesRepo.getInstance(
        getCategoriesRemoteSource()
    )

    private fun getCategoriesUseCase() = GetCategoriesUseCase(getCategoriesRepo())

    // SubCategories
    private fun getSubCategoriesRemoteSource() = SubCategoriesRemoteSource(getApiService())
    private fun getSubCategoriesRepo() = SubCategoriesRepo.getInstance(
        getSubCategoriesRemoteSource()
    )

    private fun getSubCategoriesUseCase() = GetSubCategoriesUseCase(getSubCategoriesRepo())

    // Products
    private fun getProductsRemoteSource() = ProductsRemoteSource(getApiService())
    private fun getProductsRepo() = ProductsRepo.getInstance(
        getProductsRemoteSource()
    )

    private fun getProductsUseCase() = GetProductsUseCase(getProductsRepo())
}