package com.hmaserv.rz.data.ads

import com.hmaserv.rz.domain.*

interface IAdsRepo {
    suspend fun getSlider(): DataResource<List<Slider>>
    suspend fun getPromotions(): DataResource<List<MiniAd>>
    suspend fun getMiniAds(miniAdRequest: MiniAdRequest): DataResource<List<MiniAd>>
    suspend fun getAd(adRequest: AdRequest): DataResource<Ad>
    suspend fun createAd(token: String, createProductRequest: CreateProductRequest) : DataResource<CreateProductResponse>
    suspend fun myAds(token: String): DataResource<List<MiniAd>>
    suspend fun deleteAd(token: String, request: AdRequest): DataResource<Boolean>
    suspend fun updateAd(token: String, request: UpdateAdRequest) : DataResource<CreateProductResponse>
}