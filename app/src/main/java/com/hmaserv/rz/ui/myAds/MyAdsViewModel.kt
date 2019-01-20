package com.hmaserv.rz.ui.myAds

import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.MiniAd
import com.hmaserv.rz.domain.UiState
import com.hmaserv.rz.ui.NewBaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val DATA_MY_ADS_KEY = "my_ads"

class MyAdsViewModel : NewBaseViewModel() {

    private val myAdsUseCase = Injector.getMyAdsUseCase()

    init {
        getData()
    }

    override fun launchDataJob(): Job {
        return scope.launch(dispatcherProvider.io){
            withContext(dispatcherProvider.main){ showDataLoading() }
            val result = myAdsUseCase.get()
            withContext(dispatcherProvider.main) {
                when(result){

                    is DataResource.Success -> showSuccess(result.data)
                    is DataResource.Error -> showDataError()
                }
            }
        }
    }

    private fun showSuccess(data: List<MiniAd>) {
        _uiState.value = UiState.Success(mapOf(Pair(DATA_MY_ADS_KEY, data)))
    }
}