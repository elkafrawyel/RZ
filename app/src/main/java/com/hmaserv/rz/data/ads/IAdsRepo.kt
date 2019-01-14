package com.hmaserv.rz.data.ads

import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.MiniAd
import com.hmaserv.rz.domain.MiniAdRequest

interface IAdsRepo {
    suspend fun getMiniAds(miniAdRequest: MiniAdRequest): DataResource<List<MiniAd>>
}