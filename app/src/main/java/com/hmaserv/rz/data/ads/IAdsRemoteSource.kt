package com.hmaserv.rz.data.ads

import com.hmaserv.rz.domain.*

interface IAdsRemoteSource {
    suspend fun getMiniAds(miniAdRequest: MiniAdRequest): DataResource<List<MiniAdResponse>>
    suspend fun getAd(adRequest: AdRequest): DataResource<AdResponse>
}