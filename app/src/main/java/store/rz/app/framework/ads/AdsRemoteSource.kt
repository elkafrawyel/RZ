package store.rz.app.framework.ads

import store.rz.app.R
import store.rz.app.data.ads.IAdsRemoteSource
import store.rz.app.data.apiService.RetrofitApiService
import store.rz.app.data.apiService.RetrofitAuthApiService
import store.rz.app.domain.*
import store.rz.app.utils.Injector
import store.rz.app.utils.safeApiCall
import java.io.IOException

class AdsRemoteSource(
    private val apiService: RetrofitApiService,
    private val authApiService: RetrofitAuthApiService
) : IAdsRemoteSource {

    override suspend fun getSlider(): DataResource<List<Slider>> {
        return safeApiCall(
            call = { getSliderCall() },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_call_sliders_api)
        )
    }

    private suspend fun getSliderCall(): DataResource<List<Slider>> {
        val response = apiService.getSlider().await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_sliders_api)))
    }

    override suspend fun getPromotions(): DataResource<List<MiniAdResponse>> {
        return safeApiCall(
            call = { getPromotionsCall() },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_call_promotion_api)
        )
    }

    private suspend fun getPromotionsCall(): DataResource<List<MiniAdResponse>> {
        val response = apiService.getPromotions().await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_promotion_api)))
    }

    override suspend fun getMiniAds(miniAdRequest: MiniAdRequest): DataResource<List<MiniAdResponse>> {
        return safeApiCall(
            call = { getMiniAdsCall(miniAdRequest) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_call_miniAds_api)
        )
    }

    private suspend fun getMiniAdsCall(miniAdRequest: MiniAdRequest): DataResource<List<MiniAdResponse>> {
        val response = apiService.getMiniAds(miniAdRequest).await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_miniAds_api)))
    }

    override suspend fun getAd(adRequest: AdRequest): DataResource<AdResponse> {
        return safeApiCall(
            call = { getAdCall(adRequest) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_call_ad_api)
        )
    }

    private suspend fun getAdCall(adRequest: AdRequest): DataResource<AdResponse> {
        val response = apiService.getAd(adRequest).await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_ad_api)))
    }

    override suspend fun createAd(
        token: String,
        createProductRequest: CreateProductRequest
    ): DataResource<CreateProductResponse> {
        return safeApiCall(
            call = { createAdCall(token, createProductRequest) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_call_create_ad_api)
        )
    }

    private suspend fun createAdCall(
        token: String,
        createProductRequest: CreateProductRequest
    ): DataResource<CreateProductResponse> {
        val response = authApiService.createProduct(token, createProductRequest).await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_create_ad_api)))
    }

    override suspend fun myAds(token: String): DataResource<List<MiniAdResponse>> {
        return safeApiCall(
            call = { myAdsCall(token) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_call_create_ad_api)
        )
    }

    private suspend fun myAdsCall(
        token: String
    ): DataResource<List<MiniAdResponse>> {
        val response = authApiService.myAds(token).await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_create_ad_api)))
    }

    override suspend fun deleteAd(token: String, request: AdRequest): DataResource<Boolean> {
        return safeApiCall(
            call = { deleteAdCall(token, request) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_call_create_ad_api)
        )
    }

    private suspend fun deleteAdCall(token: String, request: AdRequest): DataResource<Boolean> {
        val response = authApiService.deleteAd(token, request).await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_create_ad_api)))
    }

    override suspend fun updateAd(
        token: String,
        request: UpdateAdRequest
    ): DataResource<CreateProductResponse> {
        return safeApiCall(
            call = { updateAdCall(token, request) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_call_create_ad_api)
        )
    }

    private suspend fun updateAdCall(
        token: String,
        request: UpdateAdRequest
    ): DataResource<CreateProductResponse> {
        val response = authApiService.updateAd(token, request).await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_create_ad_api)))
    }

    override suspend fun reviews(request: ReviewsRequest): DataResource<List<ReviewResponse>> {
        return safeApiCall(
            call = { reviewsCall(request) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_http_reviews_api)
        )
    }

    private suspend fun reviewsCall(request: ReviewsRequest):
            DataResource<List<ReviewResponse>> {
        val response = apiService.getReviews(request).await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_reviews_api)))
    }

    override suspend fun writeReviews(token: String, request: WriteReviewRequest): DataResource<Boolean> {
        return safeApiCall(
            call = { writeReviewCall(token, request) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_http_write_reviews_api)
        )
    }

    private suspend fun writeReviewCall(token: String, request: WriteReviewRequest): DataResource<Boolean> {
        val response = authApiService.writeReview(token, request).await()
        if (response.success != null && response.success) {
            return DataResource.Success(response.success)
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_write_reviews_api)))

    }

    override suspend fun search(request: SearchRequest): DataResource<List<MiniAdResponse>> {
        return safeApiCall(
            call = { searchCall(request) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_call_miniAds_api)
        )
    }

    private suspend fun searchCall(request: SearchRequest):DataResource<List<MiniAdResponse>>{
        val response = apiService.search(request).await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_miniAds_api)))
    }
}