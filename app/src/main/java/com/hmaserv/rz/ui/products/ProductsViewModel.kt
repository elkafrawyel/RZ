package com.hmaserv.rz.ui.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.NetworkUtils
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.Event
import com.hmaserv.rz.domain.MiniAd
import com.hmaserv.rz.ui.BaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductsViewModel : BaseViewModel() {
    private var getProductsJob: Job? = null
    private val getProductsUseCase = Injector.getAdsUseCase()

    private val _uiState = MutableLiveData<Event<ProductsUiState>>()
    val uiState: LiveData<Event<ProductsUiState>>
        get() = _uiState

    private var subCategoryUuid: String? = null

    fun setSubCategoryId(uuid: String) {
        if (this.subCategoryUuid == null) {
            this.subCategoryUuid = uuid
            getProducts(uuid)
        }
    }

    private fun getProducts(uuid: String) {
        if (getProductsJob?.isActive == true) {
            return
        }
        getProductsJob = launchGetProductsJob(uuid)
    }

    fun refreshProducts() {
        if (getProductsJob?.isActive == true) {
            return
        }

        getProductsJob = subCategoryUuid?.let { launchGetProductsJob(it) }
    }

    private fun launchGetProductsJob(uuid: String): Job? {
        return scope.launch(dispatcherProvider.computation) {
            withContext(dispatcherProvider.main) { showLoading() }
            if (NetworkUtils.isConnected()) {
                val result = getProductsUseCase.get(uuid)
                withContext(dispatcherProvider.main) {
                    when (result) {
                        is DataResource.Success ->
                            if (result.data.isEmpty()) {
                                showEmptyView()
                            } else {
                                showSuccess(result.data)
                            }
                        is DataResource.Error -> showError(result.exception.message)
                    }
                }
            } else {
                withContext(dispatcherProvider.main) {
                    showNoInternetConnection()
                }
            }
        }
    }


    private fun showLoading() {
        _uiState.value = Event(ProductsUiState.Loading)
    }

    private fun showSuccess(data: List<MiniAd>) {
        _uiState.value = Event(ProductsUiState.Success(data))
    }

    private fun showError(message: String?) {
        if (message != null) _uiState.value = Event(ProductsUiState.Error(message))
        else _uiState.value = Event(
            ProductsUiState.Error(
                Injector.getApplicationContext().getString(R.string.error_general)
            )
        )
    }

    private fun showNoInternetConnection() {
        _uiState.value = Event(ProductsUiState.NoInternetConnection)
    }

    private fun showEmptyView() {
        _uiState.value = Event(ProductsUiState.EmptyView)
    }

    sealed class ProductsUiState {
        object Loading : ProductsUiState()
        data class Success(val ads: List<MiniAd>) : ProductsUiState()
        data class Error(val message: String) : ProductsUiState()
        object NoInternetConnection : ProductsUiState()
        object EmptyView : ProductsUiState()
    }
}