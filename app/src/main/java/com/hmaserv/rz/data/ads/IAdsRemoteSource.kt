package com.hmaserv.rz.data.ads

import com.hmaserv.rz.domain.*

interface IAdsRemoteSource {
    suspend fun getSlider(): DataResource<List<Slider>>
    suspend fun getPromotions(): DataResource<List<MiniAdResponse>>
    suspend fun getMiniAds(miniAdRequest: MiniAdRequest): DataResource<List<MiniAdResponse>>
    suspend fun getAd(adRequest: AdRequest): DataResource<AdResponse>
    suspend fun createAd(token: String, createProductRequest: CreateProductRequest) : DataResource<CreateProductResponse>
    suspend fun myAds(token: String): DataResource<List<MiniAd>>
}