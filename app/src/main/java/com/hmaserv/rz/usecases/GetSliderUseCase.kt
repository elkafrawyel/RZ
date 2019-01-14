package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.home.IHomeRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.Slider

class GetSliderUseCase(
    private val homeRepo: IHomeRepo
) {
    suspend fun getSlider(): DataResource<List<Slider>> {
        return homeRepo.getSlider()
    }
}