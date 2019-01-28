package com.hmaserv.rz.ui.home

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.NetworkUtils
import com.hmaserv.rz.domain.*
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

    private val _homeState = MutableLiveData<State.HomeState>()
    val homeState: LiveData<State.HomeState>
        get() = _homeState

    var isList = true

    init {
        _homeState.value = State.HomeState(loadingVisibility = View.VISIBLE)
        if (NetworkUtils.isConnected()) {
            getData()
        } else {
            _homeState.value = State.HomeState(noConnectionVisibility = View.VISIBLE)
        }
    }


    override fun launchDataJob(): Job {
        return scope.launch(dispatcherProvider.computation) {
            withContext(dispatcherProvider.main) {
                showDataLoading()
            }
            val sliderJob = async(dispatcherProvider.io) { getSliderUseCase.get() }
            val promotionsJob = async(dispatcherProvider.io) { getPromotionsUseCase.get() }

            val sliderResult = sliderJob.await()
            val promotionsResult = promotionsJob.await()

            withContext(dispatcherProvider.main) {
                if (sliderResult is DataResource.Success && promotionsResult is DataResource.Success) {
                    showDataSuccess(sliderResult.data, promotionsResult.data)
//                    if (promotionsResult.data.isNotEmpty()) {
//                        _homeState.value = State.HomeState(
//                            dataVisibility = View.VISIBLE,
//                            bannersVisibility = if (sliderResult.data.isEmpty()) View.GONE else View.VISIBLE,
//                            banners = sliderResult.data.mapNotNull { it.image },
//                            promotions = promotionsResult.data
//                        )
//                    } else {
                        _homeState.value = State.HomeState(
                            emptyVisibility = View.VISIBLE
                        )
//                    }
                } else if (promotionsResult is DataResource.Success) {
                    showDataSuccess(emptyList(), promotionsResult.data)
                    if (promotionsResult.data.isNotEmpty()) {
                        _homeState.value = State.HomeState(
                            dataVisibility = View.VISIBLE,
                            bannersVisibility = View.GONE,
                            promotions = promotionsResult.data
                        )
                    } else {
                        _homeState.value = State.HomeState(
                            emptyVisibility = View.VISIBLE
                        )
                    }
                } else {
                    showDataError()
                    _homeState.value = State.HomeState(
                        errorVisibility = View.VISIBLE,
                        errorPlayAnimation = true
                    )
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