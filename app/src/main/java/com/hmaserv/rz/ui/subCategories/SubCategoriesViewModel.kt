package com.hmaserv.rz.ui.subCategories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.NetworkUtils
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.Event
import com.hmaserv.rz.domain.SubCategory
import com.hmaserv.rz.ui.BaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubCategoriesViewModel : BaseViewModel() {
    private var getSubCategoriesJob: Job? = null
    private val getSubCategoriesUseCase = Injector.getSubCategoriesUseCase()
    private val _uiState = MutableLiveData<Event<SubCategoriesUiState>>()
    val uiState: LiveData<Event<SubCategoriesUiState>>
        get() = _uiState

    init {
        getSubCategories()
    }

    fun getSubCategories() {
        if (getSubCategoriesJob?.isActive == true) {
            return
        }
        getSubCategoriesJob = launchGetSubCategoriesJob()
    }

    private fun launchGetSubCategoriesJob(): Job {
        return scope.launch(dispatcherProvider.computation) {
            withContext(dispatcherProvider.main) { showLoading() }
            if (NetworkUtils.isConnected()) {
                val result = getSubCategoriesUseCase.get()
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
        _uiState.value = Event(SubCategoriesUiState.Loading)
    }

    private fun showSuccess(data: List<SubCategory>) {
        _uiState.value = Event(SubCategoriesUiState.Success(data))
    }

    private fun showError(message: String?) {
        if (message != null) _uiState.value = Event(SubCategoriesUiState.Error(message))
        else _uiState.value = Event(
            SubCategoriesUiState.Error(
                Injector.getApplicationContext().getString(R.string.error_general)
            )
        )
    }
    private fun showEmptyView() {
        _uiState.value = Event(SubCategoriesUiState.EmptyView)
    }

    private fun showNoInternetConnection() {
        _uiState.value = Event(SubCategoriesUiState.NoInternetConnection)
    }


    sealed class SubCategoriesUiState {
        object Loading : SubCategoriesUiState()
        data class Success(val categories: List<SubCategory>) : SubCategoriesUiState()
        data class Error(val message: String) : SubCategoriesUiState()
        object NoInternetConnection : SubCategoriesUiState()
        object EmptyView : SubCategoriesUiState()
    }
}