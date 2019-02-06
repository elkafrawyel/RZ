package store.rz.app.ui.search

import android.view.View
import kotlinx.coroutines.*
import store.rz.app.utils.Injector
import store.rz.app.domain.*
import store.rz.app.ui.RzBaseViewModel

class SearchViewModel :
    RzBaseViewModel<State.SearchState, String>() {

    private var searchJob: Job? = null
    private var subCategoriesJob: Job? = null

    private val savedCategoriesUseCase = Injector.getSavedCategoriesUseCase()
    private val savedSubCategoriesUseCase = Injector.getSavedSubCategoriesUseCase()

    init {
        sendAction(Action.Started)
    }

    override fun actOnAction(action: Action) {
        when (action) {
            Action.Started -> {
                getData()
            }
            is Action.CategorySelected -> {
                getSubCategories(action.position)
            }
            is Action.SubCategorySelected -> {
                state.value?.let {
                    sendState(it.copy(selectedSubCategory = action.position))
                }
            }
            else -> {
            }
        }
    }

    private fun getData() {
        if (searchJob?.isActive == true) {
            return
        }

        searchJob = launchDataJob()
    }

    private fun launchDataJob(): Job {
        return launch(dispatcherProvider.computation) {
            sendStateOnMain { State.SearchState(loadingVisibility = View.VISIBLE) }
            val categories = savedCategoriesUseCase.get()
            if (categories.isNotEmpty()) {
                sendStateOnMain { State.SearchState(dataVisibility = View.VISIBLE, categories = categories) }
            } else {
                sendStateOnMain { State.SearchState(errorVisibility = View.VISIBLE) }
            }
        }
    }

    private fun getSubCategories(position: Int) {
        state.value?.categories?.get(position)?.let {
            subCategoriesJob?.cancel()
            subCategoriesJob = launchSubCategoriesJob(it.uuid)
        }
    }

    private fun launchSubCategoriesJob(uuid: String): Job {
        return launch(dispatcherProvider.computation) {
            val subCategories = savedSubCategoriesUseCase.get(uuid)
            sendStateOnMain { State.SearchState(
                dataVisibility = View.VISIBLE,
                categories = state.value?.categories ?: emptyList(),
                subCategories = subCategories
            ) }
        }
    }

}