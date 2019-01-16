package com.hmaserv.rz.ui.createAd

import android.content.ClipData
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.NetworkUtils
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.*
import com.hmaserv.rz.ui.BaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateAdViewModel : BaseViewModel() {

    private var getSavedCategoriesJob: Job? = null
    private var getSavedSubCategoriesJob: Job? = null
    private var getAttributesJob: Job? = null

    private val getSavedCategoriesUseCase = Injector.getSavedCategoriesUseCase()
    private val getSavedSubCategoriesUseCase = Injector.getSavedSubCategoriesUseCase()
    private val getAttributesUseCase = Injector.getAttributesUseCase()

    private val _categoriesUiState = MutableLiveData<Event<CategoriesUiState>>()
    val categoriesUiState: LiveData<Event<CategoriesUiState>>
        get() = _categoriesUiState

    private val _subCategoriesUiState = MutableLiveData<Event<SubCategoriesUiState>>()
    val subCategoriesUiState: LiveData<Event<SubCategoriesUiState>>
        get() = _subCategoriesUiState

    private val _attributesUiState = MutableLiveData<Event<AttributesUiState>>()
    val attributesUiState: LiveData<Event<AttributesUiState>>
        get() = _attributesUiState

    private val selectedImagesList = ArrayList<Uri>(10)
    val attributes = ArrayList<AttributeSection>()

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

    fun getAttributes(subCategoryUuid: String) {
        if (getAttributesJob?.isActive == true) {
            return
        }
        getAttributesJob = launchGetAttributesJob(subCategoryUuid)
    }

    private fun launchGetAttributesJob(subCategoryUuid: String): Job {
        return scope.launch(dispatcherProvider.io) {
            withContext(dispatcherProvider.main) { showAttributesLoading() }
            if (NetworkUtils.isConnected()) {
                val result = getAttributesUseCase.get(subCategoryUuid)
                val attributesResult = ArrayList<Attribute>()
                withContext(dispatcherProvider.computation) {
                    when(result) {
                        is DataResource.Success -> {
                            val attributes = result.data
                            attributes.forEach { main ->
                                attributesResult.add(main)
                                main.attributes.forEach { sub ->
                                    attributesResult.add(sub)
                                }
                            }
                        }
                    }
                }
                withContext(dispatcherProvider.main) {
                    when (result) {
                        is DataResource.Success ->
                            if (result.data.isEmpty()) {
                                showAttributesEmptyView()
                            } else {
                                showAttributesSuccess(attributesResult)
                            }
                        is DataResource.Error -> showAttributesError(result.exception.message)
                    }
                }
            } else {
                withContext(dispatcherProvider.main) {
                    showAttributesNoInternetConnection()
                }
            }
        }
    }

    private fun showCategoryLoading() {
        _categoriesUiState.value = Event(CategoriesUiState.Loading)
    }

    private fun showCategorySuccess(data: List<Category>) {
        _categoriesUiState.value = Event(CategoriesUiState.Success(data))
    }

    private fun showSubCategoryLoading() {
        _subCategoriesUiState.value = Event(SubCategoriesUiState.Loading)
    }

    private fun showSubCategorySuccess(data: List<SubCategory>) {
        _subCategoriesUiState.value = Event(SubCategoriesUiState.Success(data))
    }

    private fun showAttributesLoading() {
        _attributesUiState.value = Event(AttributesUiState.Loading)
    }

    private fun showAttributesSuccess(data: List<Attribute>) {
        _attributesUiState.value = Event(AttributesUiState.Success(data))
    }

    private fun showAttributesError(message: String?) {
        if (message != null) _attributesUiState.value = Event(AttributesUiState.Error(message))
        else _attributesUiState.value = Event(
            AttributesUiState.Error(
                Injector.getApplicationContext().getString(R.string.error_general)
            )
        )
    }
    private fun showAttributesEmptyView() {
        _attributesUiState.value = Event(AttributesUiState.EmptyView)
    }

    private fun showAttributesNoInternetConnection() {
        _attributesUiState.value = Event(AttributesUiState.NoInternetConnection)
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

    fun getSelectedAttributes(): ArrayList<Attribute.MainAttribute> {
        val map = HashMap<String, ArrayList<Attribute.SubAttribute>>()
        attributes
            .filter { !it.isHeader && it.t?.isChecked == true }
            .forEach {
                val value = map[it.t.mainAttributeName]
                if (value != null) {
                    value.add(it.t)
                } else {
                    map[it.t.mainAttributeName] = arrayListOf(it.t)
                }
            }
        return ArrayList(map.map { Attribute.MainAttribute(it.key, it.value) })
    }

    sealed class CategoriesUiState {
        object Loading : CategoriesUiState()
        data class Success(val categories: List<Category>) : CategoriesUiState()
    }

    sealed class SubCategoriesUiState {
        object Loading : SubCategoriesUiState()
        data class Success(val subCategories: List<SubCategory>) : SubCategoriesUiState()
    }

    sealed class AttributesUiState {
        object Loading : AttributesUiState()
        data class Success(val attributes: List<Attribute>) : AttributesUiState()
        data class Error(val message: String) : AttributesUiState()
        object NoInternetConnection : AttributesUiState()
        object EmptyView : AttributesUiState()
    }
}