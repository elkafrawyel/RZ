package com.hmaserv.rz.ui.createOrder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.NetworkUtils
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Attribute
import com.hmaserv.rz.domain.City
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.Payment
import com.hmaserv.rz.ui.BaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateOrderViewModel : BaseViewModel() {

    private var dataJob: Job? = null
    private var createOrderJob: Job? = null

    private val getCitiesUseCase = Injector.getCitiesUseCase()
    private val getCreateOrderUseCase = Injector.createOrderUseCase()

    private val _dataState = MutableLiveData<DataState>()
    val dataState: LiveData<DataState>
        get() = _dataState

    private val _uiState = MutableLiveData<CreateOrderUiState>()
    val uiState: LiveData<CreateOrderUiState>
        get() = _uiState

    init {
        getData()
    }

    fun getData() {
        if (NetworkUtils.isConnected()) {
            if (dataJob?.isActive == true) {
                return
            }

            dataJob = launchGetData()
        } else {
            _dataState.value = DataState.NoInternetConnection
        }
    }

    private fun launchGetData() = scope.launch(dispatcherProvider.io) {
        withContext(dispatcherProvider.main) { _dataState.value = DataState.Loading }
        val dataResult = getCitiesUseCase.getCities()
        when(dataResult) {
            is DataResource.Success -> withContext(dispatcherProvider.main) { _dataState.value = DataState.Success(dataResult.data) }
            is DataResource.Error -> withContext(dispatcherProvider.main) { _dataState.value = DataState.Error }
        }
    }

    fun createOrder(
        adUuid: String,
        name: String,
        address: String,
        city: String,
        mobile: String,
        notes: String,
        attributes: List<Attribute.MainAttribute>,
        payment: Payment
    ) {
        if (NetworkUtils.isConnected()) {
            if (createOrderJob?.isActive == true) {
                return
            }

            createOrderJob = launchCreateOrderJob(
                adUuid,
                name,
                address,
                city,
                mobile,
                notes,
                attributes,
                payment
            )
        } else {
            showNoInternetConnection()
        }
    }

    private fun launchCreateOrderJob(
        adUuid: String,
        name: String,
        address: String,
        city: String,
        mobile: String,
        notes: String,
        attributes: List<Attribute.MainAttribute>,
        payment: Payment
    ): Job {
        return scope.launch(dispatcherProvider.io) {
            withContext(dispatcherProvider.main) { showDataLoading() }
            val result = getCreateOrderUseCase.create(
                adUuid,
                name,
                address,
                city,
                mobile,
                notes,
                attributes,
                payment
            )
            withContext(dispatcherProvider.main) {
                when (result) {
                    is DataResource.Success -> showSuccess(result.data)
                    is DataResource.Error -> showDataError()
                }
            }
        }
    }

    private fun showSuccess(ifCreated: Boolean) {
        _uiState.value = CreateOrderUiState.Success(ifCreated)
    }

    private fun showDataLoading() {
        _uiState.value = CreateOrderUiState.Loading
    }

    private fun showDataError() {
        _uiState.value = CreateOrderUiState.Error(Injector.getApplicationContext().getString(R.string.error_general))
    }

    private fun showNoInternetConnection() {
        _uiState.value = CreateOrderUiState.NoInternetConnection
    }

    sealed class DataState {
        object Loading : DataState()
        data class Success(val cities: List<City>) : DataState()
        object Error : DataState()
        object NoInternetConnection : DataState()
    }

    sealed class CreateOrderUiState {
        object Loading : CreateOrderUiState()
        data class Success(val ifCreated: Boolean) : CreateOrderUiState()
        data class Error(val message: String) : CreateOrderUiState()
        object NoInternetConnection : CreateOrderUiState()
    }
}