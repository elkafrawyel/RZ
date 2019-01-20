package com.hmaserv.rz.ui.home

import com.hmaserv.rz.domain.*
import com.hmaserv.rz.ui.BaseViewModel
import com.hmaserv.rz.ui.NewBaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val DATA_SLIDER_KEY = "sliders"
const val DATA_PROMOTIONS_KEY = "promotions"

class HomeViewModel : NewBaseViewModel() {

    private val getSliderUseCase = Injector.getSliderUseCase()
    private val getPromotionsUseCase = Injector.getPromotionsUseCase()

    var isList = true

    init {
        getData()
    }


    override fun launchDataJob(): Job {
        return scope.launch(dispatcherProvider.computation) {
            withContext(dispatcherProvider.main) { showDataLoading() }
            val sliderJob = async(dispatcherProvider.io) { getSliderUseCase.get() }
            val promotionsJob = async(dispatcherProvider.io) { getPromotionsUseCase.get() }

            val sliderResult = sliderJob.await()
            val promotionsResult = promotionsJob.await()

            withContext(dispatcherProvider.main) {
                if (sliderResult is DataResource.Success && promotionsResult is DataResource.Success) {
                    showDataSuccess(sliderResult.data, promotionsResult.data)
                } else {
                    showDataError()
                }
            }
        }
    }

    private fun showDataSuccess(
        sliders: List<Slider>,
        promotions: List<MiniAd>
    ) {
        _uiState.value = UiState.Success(
            mapOf(
                Pair(DATA_SLIDER_KEY, sliders),
                Pair(DATA_PROMOTIONS_KEY, promotions)
            )
        )
    }
}