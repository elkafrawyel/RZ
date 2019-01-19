package com.hmaserv.rz.ui.subCategories

import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.SubCategory
import com.hmaserv.rz.domain.UiState
import com.hmaserv.rz.ui.NewBaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val DATA_SUB_CATEGORIES_KEY = "subCategories"

class SubCategoriesViewModel : NewBaseViewModel() {

    private val getSubCategoriesUseCase = Injector.getSubCategoriesUseCase()

    private var categoryUuid: String? = null

    fun setCategoryId(categoryUuid: String) {
        if (this.categoryUuid == null) {
            this.categoryUuid = categoryUuid
            getData()
        }
    }

    override fun launchDataJob(): Job {
        return scope.launch(dispatcherProvider.io) {
            withContext(dispatcherProvider.main) { showDataLoading() }
            if (categoryUuid != null) {
                val result = getSubCategoriesUseCase.get(categoryUuid!!)
                withContext(dispatcherProvider.main) {
                    when (result) {
                        is DataResource.Success -> showSuccess(result.data)
                        is DataResource.Error -> showDataError()
                    }
                }
            } else {
                withContext(dispatcherProvider.main) { showDataError() }
            }
        }
    }

    private fun showSuccess(data: List<SubCategory>) {
        _uiState.value = UiState.Success(mapOf(Pair(DATA_SUB_CATEGORIES_KEY, data)))
    }

}