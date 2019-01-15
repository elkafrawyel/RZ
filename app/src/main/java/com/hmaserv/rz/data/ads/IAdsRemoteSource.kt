package com.hmaserv.rz.data.ads

import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.ApiMiniAd
import com.hmaserv.rz.domain.MiniAdRequest

interface IAdsRemoteSource {
    suspend fun getMiniAds(miniAdRequest: MiniAdRequest): DataResource<List<ApiMiniAd>>
}