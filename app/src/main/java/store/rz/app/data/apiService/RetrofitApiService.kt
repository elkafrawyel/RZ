package store.rz.app.data.apiService

import store.rz.app.domain.*
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface RetrofitApiService {

    @GET("lookups/cities")
    fun getCities(): Deferred<ApiResponse<List<CityResponse>>>

    @GET("lookups/orderStatuses")
    @Headers("Accept-Language: en")
    fun getOrderStatus(): Deferred<ApiResponse<List<OrderStatusResponse>>>

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

    @GET("category/all")
    fun getCategories(): Deferred<ApiResponse<List<CategoryResponse>>>

    @POST("category/subs")
    fun getSubCategories(@Body subCategoryRequest: SubCategoryRequest): Deferred<ApiResponse<List<SubCategoryResponse>>>

    @POST("subCategories/ads")
    fun getMiniAds(@Body miniAdRequest: MiniAdRequest): Deferred<ApiResponse<List<MiniAdResponse>>>

    @POST("subCategories/characteristics")
    fun getAttributes(@Body attributesRequest: AttributesRequest): Deferred<ApiResponse<List<MainAttributeResponse>>>

    @POST("ads/details")
    fun getAd(@Body adRequest: AdRequest): Deferred<ApiResponse<AdResponse>>

    @POST("ads/review/list")
    fun getReviews(@Body reviewsRequest: ReviewsRequest): Deferred<ApiResponse<List<ReviewResponse>>>

    @POST("ads/search")
    fun search(@Body searchRequest: SearchRequest): Deferred<ApiResponse<List<MiniAdResponse>>>

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