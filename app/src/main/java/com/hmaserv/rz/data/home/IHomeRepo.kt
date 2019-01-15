package com.hmaserv.rz.data.home

import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.MiniAd
import com.hmaserv.rz.domain.Slider

interface IHomeRepo {
    suspend fun getSlider(): DataResource<List<Slider>>
    suspend fun getPromotions(): DataResource<List<MiniAd>>
}