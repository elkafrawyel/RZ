package com.hmaserv.rz.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.NetworkUtils
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.*
import com.hmaserv.rz.ui.BaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : BaseViewModel() {

    private var sliderJob: Job? = null
    private var promotionsJob: Job? = null

    private val getSliderUseCase = Injector.getSliderUseCase()
    private val getPromotionsUseCase = Injector.getPromotionsUseCase()

    private val _sliderState = MutableLiveData<Event<SliderState>>()
    val sliderState : LiveData<Event<SliderState>>
        get() = _sliderState

    private val _promotionsState = MutableLiveData<Event<PromotionsState>>()
    val promotionsState : LiveData<Event<PromotionsState>>
        get() = _promotionsState

    init {
        getSlider()
        getPromotions()
    }

    private fun getSlider() {
        if (NetworkUtils.isConnected()) {
            if (sliderJob?.isActive == true) {
                return
            }

            sliderJob = launchSliderJob()
        } else {
            showSliderNoInternetConnection()
        }
    }

    private fun launchSliderJob(): Job {
        return scope.launch(dispatcherProvider.io) {
            withContext(dispatcherProvider.main) { showSliderLoading() }
            val result = getSliderUseCase.getSlider()
            withContext(dispatcherProvider.main) {
                when(result) {
                    is DataResource.Success -> {
                        if (result.data.isEmpty()) showSliderEmpty()
                        else showSliderSuccess(result.data)
                    }
                    is DataResource.Error -> showSliderError(result.exception.message)
                }
            }
        }
    }

    private fun getPromotions() {
        if (NetworkUtils.isConnected()) {
            if (promotionsJob?.isActive == true) {
                return
            }

            promotionsJob = launchPromotionsJob()
        } else {
            showPromotionsNoInternetConnection()
        }
    }

    private fun launchPromotionsJob(): Job {
        return scope.launch(dispatcherProvider.io) {
            withContext(dispatcherProvider.main) { showPromotionsLoading() }
            val result = getPromotionsUseCase.getPromotions()
            withContext(dispatcherProvider.main) {
                when(result) {
                    is DataResource.Success -> {
                        if (result.data.isEmpty()) showPromotionsEmpty()
                        else showPromotionsSuccess(result.data)
                    }
                    is DataResource.Error -> showPromotionsError(result.exception.message)
                }
            }
        }
    }

    private fun showSliderLoading() {
        _sliderState.value = Event(SliderState.Loading)
    }

    private fun showSliderSuccess(sliders: List<Slider>) {
        _sliderState.value = Event(SliderState.Success(sliders))
    }

    private fun showSliderError(message: String?) {
        if (message != null) _sliderState.value = Event(SliderState.Error(message))
        else _sliderState.value = Event(SliderState.Error(Injector.getApplicationContext().getString(R.string.error_general)))
    }


    private fun showSliderEmpty() {
        _sliderState.value = Event(SliderState.Empty)
    }

    private fun showSliderNoInternetConnection() {
        _sliderState.value = Event(SliderState.NoInternetConnection)
    }

    private fun showPromotionsLoading() {
        _promotionsState.value = Event(PromotionsState.Loading)
    }

    private fun showPromotionsSuccess(data: List<MiniAd>) {
        _promotionsState.value = Event(PromotionsState.Success(data))
    }

    private fun showPromotionsError(message: String?) {
        if (message != null) _promotionsState.value = Event(PromotionsState.Error(message))
        else _promotionsState.value = Event(PromotionsState.Error(Injector.getApplicationContext().getString(R.string.error_general)))
    }


    private fun showPromotionsEmpty() {
        _promotionsState.value = Event(PromotionsState.Empty)
    }

    private fun showPromotionsNoInternetConnection() {
        _promotionsState.value = Event(PromotionsState.NoInternetConnection)
    }

    sealed class SliderState {
        object Loading : SliderState()
        object Empty : SliderState()
        data class Success(val sliders: List<Slider>) : SliderState()
        data class Error(val message: String) : SliderState()
        object NoInternetConnection: SliderState()
    }

    sealed class PromotionsState {
        object Loading : PromotionsState()
        object Empty : PromotionsState()
        data class Success(val promotions: List<MiniAd>) : PromotionsState()
        data class Error(val message: String) : PromotionsState()
        object NoInternetConnection: PromotionsState()
    }
}