package com.hmaserv.rz.data.home

import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.ApiMiniAd
import com.hmaserv.rz.domain.Slider

interface IHomeRemoteSource {
    suspend fun getSlider(): DataResource<List<Slider>>
    suspend fun getPromotions(): DataResource<List<ApiMiniAd>>
}