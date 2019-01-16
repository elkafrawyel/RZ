package com.hmaserv.rz.ui.createAd

import android.content.ClipData
import android.net.Uri
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

class CreateAdViewModel : BaseViewModel() {

    private var createProductJob: Job? = null
    private var getSavedCategoriesJob: Job? = null
    private var getSavedSubCategoriesJob: Job? = null

    private val selectedImagesList = ArrayList<Uri>(10)

    private val createAdUseCase = Injector.createAdUseCase()
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

    fun createProductTest() {
        if (createProductJob?.isActive == true) {
            return
        }

        createProductJob = launchCreateProductTest()
    }

    private fun launchCreateProductTest(): Job {
        return scope.launch(dispatcherProvider.io) {
            val result = createAdUseCase.create(
                "3f6a93ed-781b-459f-923e-9af386119690",
                "Test Android one",
                "some description",
                "1500",
                "1200",
                "100"
            )
        }
    }

    fun addSelectedImage(uri: Uri): Boolean {
        if (selectedImagesList.size < 10) {
            selectedImagesList.add(uri)
            return true
        }

        return false
    }

    fun addSelectedImages(clipData: ClipData): Boolean {
        if (selectedImagesList.size + clipData.itemCount < 11) {
            for (i in 0 until clipData.itemCount) {
                selectedImagesList.add(clipData.getItemAt(i).uri)
            }
            return true
        }

        return false
    }

    fun getSelectedImagesList(): ArrayList<Uri> {
        return selectedImagesList
    }

    fun getSelectedImagesStringList(): ArrayList<String> {
        return ArrayList(selectedImagesList.map { it.toString() })
    }

    fun getSelectedImagesSize(): Int {
        return selectedImagesList.size
    }

    fun removeUri(position: Int) {
        selectedImagesList.removeAt(position)
    }
}