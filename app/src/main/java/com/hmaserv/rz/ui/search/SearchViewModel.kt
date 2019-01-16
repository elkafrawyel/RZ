package com.hmaserv.rz.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hmaserv.rz.domain.Category
import com.hmaserv.rz.domain.Event
import com.hmaserv.rz.domain.SubCategory
import com.hmaserv.rz.ui.BaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel : BaseViewModel() {
    private var getSavedCategoriesJob: Job? = null
    private var getSavedSubCategoriesJob: Job? = null

    private val getSavedCategoriesUseCase = Injector.getSavedCategoriesUseCase()
    private val getSavedSubCategoriesUseCase = Injector.getSavedSubCategoriesUseCase()

    private val _categoriesUiState = MutableLiveData<Event<CategoriesUiState>>()
    val categoriesUiState: LiveData<Event<CategoriesUiState>>
        get() = _categoriesUiState

    private val _subCategoriesUiState = MutableLiveData<Event<SubCategoriesUiState>>()
    val subCategoriesUiState: LiveData<Event<SubCategoriesUiState>>
        get() = _subCategoriesUiState

    init {
        getSavedCategories()
    }

    private fun getSavedCategories() {
        if (getSavedCategoriesJob?.isActive == true) {
            return
        }

        getSavedCategoriesJob = launchGetSavedCategoriesJob()
    }

    private fun launchGetSavedCategoriesJob(): Job? {
        return scope.launch(dispatcherProvider.computation) {
            withContext(dispatcherProvider.main) { showCategoryLoading() }
            val data = getSavedCategoriesUseCase.get()
            withContext(dispatcherProvider.main) {
                showCategorySuccess(data)
            }
        }
    }

    fun getSavedSubCategories(categoryUuid: String) {
        if (getSavedCategoriesJob?.isActive == true) {
            return
        }

        getSavedSubCategoriesJob = launchGetSavedCategoriesJob(categoryUuid)
    }

    private fun launchGetSavedCategoriesJob(categoryUuid: String): Job? {
        return scope.launch(dispatcherProvider.computation) {
            withContext(dispatcherProvider.main) { showSubCategoryLoading() }
            val data = getSavedSubCategoriesUseCase.get(categoryUuid)
            withContext(dispatcherProvider.main) {
                showSubCategorySuccess(data)
            }
        }
    }

    private fun showCategoryLoading() {
        _categoriesUiState.value = Event(CategoriesUiState.Loading)
    }

    private fun showCategorySuccess(data: List<Category>) {
        _categoriesUiState.value = Event(CategoriesUiState.Success(data))
    }

    sealed class CategoriesUiState {
        object Loading : CategoriesUiState()
        data class Success(val categories: List<Category>) : CategoriesUiState()
    }

    private fun showSubCategoryLoading() {
        _subCategoriesUiState.value = Event(SubCategoriesUiState.Loading)
    }

    private fun showSubCategorySuccess(data: List<SubCategory>) {
        _subCategoriesUiState.value = Event(SubCategoriesUiState.Success(data))
    }

    sealed class SubCategoriesUiState {
        object Loading : SubCategoriesUiState()
        data class Success(val subCategories: List<SubCategory>) : SubCategoriesUiState()
    }
}