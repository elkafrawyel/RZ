package store.rz.app.utils

import android.util.Log
import com.facebook.spectrum.DefaultPlugins
import com.facebook.spectrum.Spectrum
import com.facebook.spectrum.logging.SpectrumLogcatLogger
import store.rz.app.RzApplication
import store.rz.app.data.apiService.RetrofitApiService
import store.rz.app.data.apiService.RetrofitAuthApiService
import store.rz.app.framework.ads.AdsRemoteSource
import store.rz.app.framework.ads.AdsRepo
import store.rz.app.framework.categories.CategoriesLocalSource
import store.rz.app.framework.categories.CategoriesRemoteSource
import store.rz.app.framework.categories.CategoriesRepo
import store.rz.app.framework.logedInUser.LoggedInUserLocalSource
import store.rz.app.framework.logedInUser.LoggedInUserRemoteSource
import store.rz.app.framework.logedInUser.LoggedInUserRepo
import store.rz.app.framework.lookups.LookUpsRemoteSource
import store.rz.app.framework.lookups.LookUpsRepo
import store.rz.app.framework.orders.OrdersRemoteSource
import store.rz.app.framework.orders.OrdersRepo
import store.rz.app.framework.settings.SettingsLocalSource
import store.rz.app.framework.settings.SettingsRepo
import store.rz.app.framework.subCategories.SubCategoriesLocalSource
import store.rz.app.framework.subCategories.SubCategoriesRemoteSource
import store.rz.app.framework.subCategories.SubCategoriesRepo
import store.rz.app.framework.uploader.UploaderRemoteSource
import store.rz.app.framework.uploader.UploaderRepo
import store.rz.app.usecases.*
import store.rz.app.utils.Constants.BASE_URL
import store.rz.app.utils.Constants.Language
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

object Injector {

    fun init() {
        getApiService()
        getAuthApiService()
        getBoxStore()
    }

    var language = Language.DEFAULT
        set(value) {
            field = value
            LoggedInUserRepo.resetRemoteSource(getLoggedInRemoteSource())
        }

    private val mSpectrum: Spectrum = Spectrum.make(
        SpectrumLogcatLogger(Log.INFO),
        DefaultPlugins.get()
    )

    private val coroutinesDispatcherProvider = CoroutinesDispatcherProvider(
        Dispatchers.Main,
        Dispatchers.Default,
        Dispatchers.IO
    )

    fun getApplicationContext() = RzApplication.instance
    fun getCoroutinesDispatcherProvider() = coroutinesDispatcherProvider
    private fun getLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private fun getApiServiceHeader(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("Accept", "application/json")

            if (chain.request().header("Accept-Language") == null) {
                request.addHeader(
                    "Accept-Language",
                    chain.request().header("Accept-Language") ?: language.value
                )
            }

            chain.proceed(request.build())
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
    fun getSpectrum() = mSpectrum
    fun getResizedImagesDir(): File {
        val outputDir = File(getApplicationContext().filesDir, Constants.RESIZED_IMAGES_OUTPUT_PATH)
        if (!outputDir.exists()) {
            outputDir.mkdirs() // should succeed
        }
        return outputDir
    }

    private fun generateResizedImageName(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyyMMDD_HHmmss_SSS", Locale.ENGLISH)
        return dateFormat.format(calendar.time) ?: "resizeImage${Random.nextInt()}"
    }

    fun getNewResizedImagePath(): String? {
        val imageName = "${generateResizedImageName()}.png"
        val imageFile = File(getResizedImagesDir(), imageName)
        return imageFile.path
    }

    // Settings
    private fun getSettingsLocalSource() = SettingsLocalSource(getBoxStore())

    private fun getSettingsRepo() = SettingsRepo.getInstance(getSettingsLocalSource())
    fun getCurrentLanguageUseCase() = CurrentLanguageUseCase(getSettingsRepo())
    fun setChangeLanguageUseCase() = ChangeLanguageUseCase(getSettingsRepo())
    fun saveFirebaseTokenUseCase() = SaveFirebaseTokenUseCase(getSettingsRepo())
    fun setAcceptedContractUseCase() = SetAcceptedContractUseCase(getSettingsRepo())
    fun isAcceptedContractUseCase() = IsAcceptedContractUseCase(getSettingsRepo())
    fun getAcceptTermsUseCase() = AcceptTermsUseCase(getLoggedInRepo(), getAuthApiService())

    // LoggedIn repo
    private fun getLoggedInRemoteSource() = LoggedInUserRemoteSource(getApiService(), getAuthApiService())

    private fun getLoggedInLocalSource() = LoggedInUserLocalSource(getBoxStore())
    private fun getLoggedInRepo() = LoggedInUserRepo.getInstance(
        getLoggedInRemoteSource(),
        getLoggedInLocalSource()
    )

    // LoggedIn use cases
    fun getLoginUseCase() = LoginUserUseCase(getSettingsRepo(), getLoggedInRepo())

    fun getRegisterUseCase() = RegisterUserUseCase(getLoggedInRepo())
    fun getVerifyPhoneUseCase() = VerifyPhoneUseCase(getLoggedInRepo())
    fun getForgetPasswordUseCase() = ForgetPasswordUseCase(getLoggedInRepo())
    fun upgradeUserUseCase() = UpgradeUserUseCase(getLoggedInRepo())
    fun getLogOutUseCase() = LogOutUseCase(getSettingsRepo(), getLoggedInRepo())
    fun getLoggedInUserListenerUseCase() = LogInListenerUseCase(getLoggedInRepo())
    fun sendFirebaseTokeUseCase() = SendFirebaseTokenUseCase(getSettingsRepo(), getLoggedInRepo())

    // Categories
    private fun getCategoriesLocalSource() = CategoriesLocalSource(getBoxStore())

    private fun getCategoriesRemoteSource() = CategoriesRemoteSource(getApiService())
    private fun getCategoriesRepo() = CategoriesRepo.getInstance(
        getCategoriesRemoteSource(),
        getCategoriesLocalSource()
    )

    fun getCategoriesUseCase() = GetCategoriesUseCase(getCategoriesRepo())
    fun getSavedCategoriesUseCase() = GetSavedCategoriesUseCase(getCategoriesRepo())

    // SubCategories
    private fun getSubCategoriesLocalSource() = SubCategoriesLocalSource(getBoxStore())

    private fun getSubCategoriesRemoteSource() = SubCategoriesRemoteSource(getApiService())
    private fun getSubCategoriesRepo() = SubCategoriesRepo.getInstance(
        getSubCategoriesLocalSource(),
        getSubCategoriesRemoteSource()
    )

    fun getSubCategoriesUseCase() = GetSubCategoriesUseCase(getSubCategoriesRepo())
    fun getSavedSubCategoriesUseCase() = GetSavedSubCategoriesUseCase(getSubCategoriesRepo())
    fun getAttributesUseCase() = GetAttributesUseCase(getSubCategoriesRepo())

    // Ads
    private fun getAdsRemoteSource() = AdsRemoteSource(getApiService(), getAuthApiService())

    private fun getAdsRepo() = AdsRepo.getInstance(
        getAdsRemoteSource()
    )

    fun getSliderUseCase() = GetSliderUseCase(getAdsRepo())
    fun getPromotionsUseCase() = GetPromotionsUseCase(getAdsRepo())
    fun getMiniAdsUseCase() = GetMiniAdsUseCase(getAdsRepo())
    fun getAdUseCase() = GetAdUseCase(getAdsRepo())
    fun createAdUseCase() = CreateAdUseCase(getLoggedInRepo(), getAdsRepo())
    fun getMyAdsUseCase() = GetMyAdsUseCase(getLoggedInRepo(), getAdsRepo())
    fun deleteAdUseCase() = DeleteAdUseCase(getLoggedInRepo(), getAdsRepo())
    fun updateAdUseCase() = UpdateAdUseCase(getLoggedInRepo(), getAdsRepo())
    fun reviewsUseCase() = ReviewsUseCase(getAdsRepo())
    fun writeReviewUseCase() = WriteReviewUseCase(getLoggedInRepo(), getAdsRepo())
    fun searchUseCase() = SearchUseCase(getAdsRepo())

    // uploader
    private fun getUploaderRemoteSource() = UploaderRemoteSource(getAuthApiService())

    private fun getUploaderRepo() = UploaderRepo.getInstance(getUploaderRemoteSource())

    fun getUploadAdImageUseCase() = UploadAdImage(getLoggedInRepo(), getUploaderRepo())
    fun deleteAdImageUseCase() = DeleteImageUseCase(getLoggedInRepo(), getUploaderRepo())

    // orders
    private fun getOrdersRemoteSource() = OrdersRemoteSource(getApiService(), getAuthApiService())

    private fun getOrdersRepo() = OrdersRepo.getInstance(getOrdersRemoteSource())

    fun createOrderUseCase() = CreateOrderUseCase(getLoggedInRepo(), getOrdersRepo())
    fun getMyOrdersUseCase() = GetMyOrdersUseCase(getLoggedInRepo(), getOrdersRepo())
    fun getReceivedOrdersUseCase() = GetReceivedOrdersUseCase(getLoggedInRepo(), getOrdersRepo())
    fun getOrderStatusUseCase() = GetOrderStatusUseCase(getOrdersRepo())
    fun getOrderUseCase() = GetOrderUseCase(getLoggedInRepo(), getOrdersRepo())
    fun orderActionUseCase() = OrderActionUseCase(getLoggedInRepo(), getOrdersRepo())

    // lookups
    private fun getLookUpsRemoteSource() = LookUpsRemoteSource(getApiService())

    private fun getLookUpsRepo() = LookUpsRepo.getInstance(getLookUpsRemoteSource())

    fun getCitiesUseCase() = GetCitiesUseCase(getLookUpsRepo())
}