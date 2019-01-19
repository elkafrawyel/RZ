package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.ads.IAdsRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.Slider

class GetSliderUseCase(
    private val adsRepo: IAdsRepo
) {
    suspend fun get(): DataResource<List<Slider>> {
        return adsRepo.getSlider()
    }
}