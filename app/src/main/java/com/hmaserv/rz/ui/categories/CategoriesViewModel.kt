package com.hmaserv.rz.ui.categories

import com.hmaserv.rz.domain.Category
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.UiState
import com.hmaserv.rz.ui.NewBaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val DATA_CATEGORIES_KEY = "categories"

class CategoriesViewModel : NewBaseViewModel() {

    private val getCategoriesUseCase = Injector.getCategoriesUseCase()

    init {
        getData()
    }

    override fun launchDataJob(): Job {
        return scope.launch(dispatcherProvider.computation) {
            withContext(dispatcherProvider.main) { showDataLoading() }
            val result = getCategoriesUseCase.get()
            withContext(dispatcherProvider.main) {
                when (result) {
                    is DataResource.Success -> showSuccess(result.data)
                    is DataResource.Error -> showDataError()
                }
            }
        }
    }

    private fun showSuccess(data: List<Category>) {
        _uiState.value = UiState.Success(mapOf(Pair(DATA_CATEGORIES_KEY, data)))
    }
}