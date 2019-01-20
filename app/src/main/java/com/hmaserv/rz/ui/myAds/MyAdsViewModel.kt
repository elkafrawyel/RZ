package com.hmaserv.rz.ui.myAds

import com.hmaserv.rz.ui.NewBaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

const val DATA_MY_ADS_KEY = "my_ads"

class MyAdsViewModel : NewBaseViewModel() {

    private val myAdsUseCase = Injector.getMyAdsUseCase()

    init {
        getData()
    }

    override fun launchDataJob(): Job {
        return scope.launch(dispatcherProvider.io){

        }
    }
}