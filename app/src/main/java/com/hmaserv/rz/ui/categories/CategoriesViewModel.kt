package com.hmaserv.rz.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.NetworkUtils
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Category
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.Event
import com.hmaserv.rz.ui.BaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriesViewModel : BaseViewModel() {
    private var getCategoriesJob: Job? = null

    private val getCategoriesUseCase = Injector.getCategoriesUseCase()

    private val _uiState = MutableLiveData<Event<CategoriesUiState>>()
    val uiState: LiveData<Event<CategoriesUiState>>
        get() = _uiState

    init {
        getCategories()
    }

    fun getCategories() {
        if (getCategoriesJob?.isActive == true) {
            return
        }
        getCategoriesJob = launchGetCategoriesJob()
    }

    private fun launchGetCategoriesJob(): Job {
        return scope.launch(dispatcherProvider.computation) {
            withContext(dispatcherProvider.main) { showLoading() }
            if (NetworkUtils.isConnected()) {
                val result = getCategoriesUseCase.get()
                withContext(dispatcherProvider.main) {
                    when (result) {
                        is DataResource.Success -> showSuccess(result.data)
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
        _uiState.value = Event(CategoriesUiState.Loading)
    }

    private fun showSuccess(data: List<Category>) {
        _uiState.value = Event(CategoriesUiState.Success(data))
    }

    private fun showError(message: String?) {
        if (message != null) _uiState.value = Event(CategoriesUiState.Error(message))
        else _uiState.value = Event(
            CategoriesUiState.Error(
                Injector.getApplicationContext().getString(R.string.error_general)
            )
        )
    }

    private fun showNoInternetConnection() {
        _uiState.value = Event(CategoriesUiState.NoInternetConnection)
    }

    sealed class CategoriesUiState {
        object Loading : CategoriesUiState()
        data class Success(val categories: List<Category>) : CategoriesUiState()
        data class Error(val message: String) : CategoriesUiState()
        object NoInternetConnection : CategoriesUiState()
    }
}