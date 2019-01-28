package com.hmaserv.rz.ui.ads

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

    private val getSearchUseCase = Injector.searchUseCase()

    private var subCategoryUuid: String? = null
    private var searchText: String? = null

    var isList = true

    override fun launchDataJob(): Job {
        return scope.launch(dispatcherProvider.computation) {
            if (NetworkUtils.isConnected()) {
                withContext(dispatcherProvider.main) { showDataLoading() }
                if (subCategoryUuid != null) {
                    val result = getSearchUseCase.search(subCategoryUuid!!,searchText!!)
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

    fun search(subUuid: String, searchText: String?) {
        if (subCategoryUuid == null){
            this.subCategoryUuid = subUuid
            this.searchText = searchText
            getData()
        }
    }

}