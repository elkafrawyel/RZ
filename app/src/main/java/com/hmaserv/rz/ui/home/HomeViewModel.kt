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

    private var upgradeRequest: Job? = null

    private val getSliderUseCase = Injector.getSliderUseCase()
    private val getPromotionsUseCase = Injector.getPromotionsUseCase()
    private val upgradeUserUseCase = Injector.upgradeUserUseCase()

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
                } else if (promotionsResult is DataResource.Success) {
                    showDataSuccess(emptyList(), promotionsResult.data)
                } else {
                    showDataError()
                }
            }
        }
    }

    fun sendUpgradeRequest() {
        if (upgradeRequest?.isActive == true) {
            return
        }

        upgradeRequest = launchSendUpgradeRequest()
    }

    private fun launchSendUpgradeRequest() = scope.launch(dispatcherProvider.io) {
        val result = upgradeUserUseCase.upgrade()
        when(result) {
            is DataResource.Success -> {}
            is DataResource.Error -> {}
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