package com.hmaserv.rz.data.ads

import com.hmaserv.rz.domain.*

interface IAdsRepo {
    suspend fun getMiniAds(miniAdRequest: MiniAdRequest): DataResource<List<MiniAd>>
    suspend fun getAd(adRequest: AdRequest): DataResource<Ad>
    suspend fun createAd(token: String, createProductRequest: CreateProductRequest) : DataResource<CreateProductResponse>
}