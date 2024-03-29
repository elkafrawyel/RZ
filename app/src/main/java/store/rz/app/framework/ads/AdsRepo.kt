package store.rz.app.framework.ads

import store.rz.app.R
import store.rz.app.data.ads.IAdsRemoteSource
import store.rz.app.data.ads.IAdsRepo
import store.rz.app.domain.*
import store.rz.app.utils.Injector
import java.io.IOException

class AdsRepo(
    private var adsRemoteSource: IAdsRemoteSource
) : IAdsRepo {


    override suspend fun getSlider(): DataResource<List<Slider>> {
        return adsRemoteSource.getSlider()
    }

    override suspend fun getPromotions(): DataResource<List<MiniAd>> {
        val result = adsRemoteSource.getPromotions()
        return when (result) {
            is DataResource.Success -> DataResource.Success(result.data.mapNotNull { it.toMiniAd() })
            is DataResource.Error -> result
        }
    }

    override suspend fun getMiniAds(miniAdRequest: MiniAdRequest): DataResource<List<MiniAd>> {
        val result = adsRemoteSource.getMiniAds(miniAdRequest)
        return when (result) {
            is DataResource.Success -> DataResource.Success(result.data.mapNotNull { it.toMiniAd() })
            is DataResource.Error -> result
        }
    }

    override suspend fun getAd(adRequest: AdRequest): DataResource<Ad> {
        val result = adsRemoteSource.getAd(adRequest)
        return when (result) {
            is DataResource.Success -> {
                val ad = result.data.toAd()
                if (ad != null)
                    DataResource.Success(ad)
                else
                    DataResource.Error(
                        IOException(
                            Injector.getApplicationContext().getString(
                                R.string.error_getting_ad
                            )
                        )
                    )
            }
            is DataResource.Error -> result
        }
    }

    override suspend fun createAd(
        token: String,
        createProductRequest: CreateProductRequest
    ): DataResource<CreateProductResponse> {
        return adsRemoteSource.createAd(token, createProductRequest)
    }

    override suspend fun myAds(token: String): DataResource<List<MiniAd>> {
        val result = adsRemoteSource.myAds(token)
        return when (result) {
            is DataResource.Success -> DataResource.Success(result.data.mapNotNull { it.toMiniAd() })
            is DataResource.Error -> result
        }
    }

    override suspend fun deleteAd(token: String, request: AdRequest): DataResource<Boolean> {
        return adsRemoteSource.deleteAd(token, request)
    }

    override suspend fun updateAd(
        token: String,
        request: UpdateAdRequest
    ): DataResource<CreateProductResponse> {
        return adsRemoteSource.updateAd(token, request)
    }

    override suspend fun reviews(request: ReviewsRequest): DataResource<List<Review>> {
        val result = adsRemoteSource.reviews(request)
        return when (result) {
            is DataResource.Success -> DataResource.Success(result.data.mapNotNull { it.toReview() })
            is DataResource.Error -> result
        }
    }

    override suspend fun writeReviews(token: String,request: WriteReviewRequest): DataResource<Boolean> {
        return adsRemoteSource.writeReviews(token,request)
    }

    override suspend fun search(request: SearchRequest): DataResource<List<MiniAd>> {
        val result = adsRemoteSource.search(request)
        return when(result){

            is DataResource.Success -> DataResource.Success(result.data.mapNotNull { it.toMiniAd() })
            is DataResource.Error -> result
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AdsRepo? = null

        fun getInstance(
            adsRemoteSource: IAdsRemoteSource
        ): AdsRepo {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AdsRepo(adsRemoteSource).also { INSTANCE = it }
            }
        }

        fun resetRemoteSource(
            adsRemoteSource: IAdsRemoteSource
        ) {
            INSTANCE?.adsRemoteSource = adsRemoteSource
        }
    }
}