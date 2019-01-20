package com.hmaserv.rz.ui.Ads

import com.blankj.utilcode.util.NetworkUtils
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.MiniAd
import com.hmaserv.rz.domain.UiState
import com.hmaserv.rz.ui.NewBaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val DATA_PRODUCTS_KEY = "products"

class AdsViewModel : NewBaseViewModel() {

    private val getMiniAdsUseCase = Injector.getMiniAdsUseCase()

    private var subCategoryUuid: String? = null

    fun setSubCategoryId(uuid: String) {
        if (this.subCategoryUuid == null) {
            this.subCategoryUuid = uuid
            getData()
        }
    }

    override fun launchDataJob(): Job {
        return scope.launch(dispatcherProvider.computation) {
            if (NetworkUtils.isConnected()) {
                withContext(dispatcherProvider.main) { showDataLoading() }
                if (subCategoryUuid != null) {
                    val result = getMiniAdsUseCase.get(subCategoryUuid!!)
                    withContext(dispatcherProvider.main) {
                        when (result) {
                            is DataResource.Success -> showSuccess(result.data)
                            is DataResource.Error -> showDataError()
                        }
                    }
                } else {
                    showDataError()
                }

            } else {
                withContext(dispatcherProvider.main) {
                    showNoInternetConnection()
                }
            }
        }
    }

    private fun showSuccess(data: List<MiniAd>) {
        _uiState.value = UiState.Success(mapOf(Pair(DATA_PRODUCTS_KEY, data)))
    }

}