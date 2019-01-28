package com.hmaserv.rz.data.ads

import com.hmaserv.rz.domain.*

interface IAdsRemoteSource {
    suspend fun getSlider(): DataResource<List<Slider>>
    suspend fun getPromotions(): DataResource<List<MiniAdResponse>>
    suspend fun getMiniAds(miniAdRequest: MiniAdRequest): DataResource<List<MiniAdResponse>>
    suspend fun getAd(adRequest: AdRequest): DataResource<AdResponse>
    suspend fun createAd(token: String, createProductRequest: CreateProductRequest) : DataResource<CreateProductResponse>
    suspend fun myAds(token: String): DataResource<List<MiniAdResponse>>
    suspend fun deleteAd(token: String, request: AdRequest): DataResource<Boolean>
    suspend fun updateAd(token: String, request: UpdateAdRequest) : DataResource<CreateProductResponse>
    suspend fun reviews(request: ReviewsRequest):DataResource<List<ReviewResponse>>
    suspend fun writeReviews(token: String,request: WriteReviewRequest):DataResource<Boolean>
}