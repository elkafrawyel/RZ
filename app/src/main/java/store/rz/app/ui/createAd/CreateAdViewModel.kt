package store.rz.app.ui.createAd

import android.content.ClipData
import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.NetworkUtils
import store.rz.app.R
import store.rz.app.domain.*
import store.rz.app.ui.RzBaseViewModel
import store.rz.app.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateAdViewModel : RzBaseViewModel<State.CreateAdState, String>() {

    //    private var dataJob: Job? = null
    private var getSavedCategoriesJob: Job? = null
    private var getSavedSubCategoriesJob: Job? = null
    private var getAttributesJob: Job? = null

    private val getSavedCategoriesUseCase = Injector.getSavedCategoriesUseCase()
    private val getSavedSubCategoriesUseCase = Injector.getSavedSubCategoriesUseCase()
    private val getAttributesUseCase = Injector.getAttributesUseCase()

    var categories = java.util.ArrayList<Category>()
    var subCategories = java.util.ArrayList<SubCategory>()

    private val _categoriesUiState = MutableLiveData<CategoriesUiState>()
    val categoriesUiState: LiveData<CategoriesUiState>
        get() = _categoriesUiState

    private val _subCategoriesUiState = MutableLiveData<SubCategoriesUiState>()
    val subCategoriesUiState: LiveData<SubCategoriesUiState>
        get() = _subCategoriesUiState

    private val _attributesUiState = MutableLiveData<Event<AttributesUiState>>()
    val attributesUiState: LiveData<Event<AttributesUiState>>
        get() = _attributesUiState

    private val selectedImagesList = ArrayList<Uri>(10)
    private var selectedVideo: String? = null
    val attributes = ArrayList<AttributeSection>()
    val dates = ArrayList<String>()

    var attributesVisibility = View.GONE
    var datesVisibility = View.GONE

    init {
        getSavedCategories()
//        sendAction(Action.Started)
    }

    override fun actOnAction(action: Action) {
//        when (action) {
//            Action.Started -> getData()
//            else -> {
//            }
//        }
    }

//    private fun getData() {
//        if (dataJob?.isActive == true) {
//            return
//        }
//
//        dataJob = launchDataJob()
//    }
//
//    private fun launchDataJob(): Job {
//        return launch {
//            sendStateOnMain { State.CreateAdState(loadingVisibility = View.VISIBLE) }
//            val categoriesResult = getSavedCategoriesUseCase.get()
//            val subCategoriesMap = HashMap<String, List<SubCategory>>()
//            val attributesMap = HashMap<String, List<AttributeSection>>()
//            categoriesResult.forEach { category ->
//                subCategoriesMap[category.uuid] = getSavedSubCategoriesUseCase.get(category.uuid)
//            }
//            subCategoriesMap.values.forEach { subCategoriesList ->
//                subCategoriesList.forEach { subCategory ->
//                    val attributesResult = getAttributesUseCase.get(subCategory.uuid)
//                    when (attributesResult) {
//                        is DataResource.Success -> {
//                            val attributesSections = ArrayList<AttributeSection>()
//                            attributesResult.data.map { mainAttribute ->
//                                attributesSections.add(AttributeSection(true, mainAttribute.name))
//                                attributesSections.addAll(mainAttribute.attributes
//                                    .map { subAttribute -> AttributeSection(subAttribute) }
//                                )
//                            }
//
//                            attributesMap[subCategory.uuid] = attributesSections
//                        }
//                        is DataResource.Error -> {}
//                    }
//                }
//            }
//
//            sendStateOnMain {
//                State.CreateAdState(
//                    dataVisibility = View.VISIBLE,
//                    categories = categoriesResult,
//                    subCategoriesMap = subCategoriesMap,
//                    attributesMap = attributesMap
//                )
//            }
//        }
//    }

    private fun getSavedCategories() {
        if (getSavedCategoriesJob?.isActive == true) {
            return
        }

        getSavedCategoriesJob = launchGetSavedCategoriesJob()
    }

    private fun launchGetSavedCategoriesJob(): Job? {
        return launch(dispatcherProvider.io) {
            withContext(dispatcherProvider.main) { showCategoryLoading() }
            val data = getSavedCategoriesUseCase.get()
            withContext(dispatcherProvider.main) {
                showCategorySuccess(data)
            }
        }
    }

    fun getSavedSubCategories(categoryUuid: String) {
        if (getSavedSubCategoriesJob?.isActive == true) {
            return
        }

        getSavedSubCategoriesJob = launchSubCategoriesJob(categoryUuid)
    }

    private fun launchSubCategoriesJob(categoryUuid: String): Job? {
        return launch(dispatcherProvider.io) {
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
        return launch(dispatcherProvider.io) {
            withContext(dispatcherProvider.main) { showAttributesLoading() }
            if (NetworkUtils.isConnected()) {
                val result = getAttributesUseCase.get(subCategoryUuid)
                val attributesResult = ArrayList<Attribute>()
                withContext(dispatcherProvider.computation) {
                    when (result) {
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
        _categoriesUiState.value = CategoriesUiState.Loading
    }

    private fun showCategorySuccess(data: List<Category>) {
        categories.clear()
        categories.addAll(data)
        _categoriesUiState.value = CategoriesUiState.Success
    }

    private fun showSubCategoryLoading() {
        _subCategoriesUiState.value = SubCategoriesUiState.Loading
    }

    private fun showSubCategorySuccess(data: List<SubCategory>) {
        subCategories.clear()
        subCategories.addAll(data)
        _subCategoriesUiState.value = SubCategoriesUiState.Success
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

    fun addSelectedVideo(videoPath: String) {
        selectedVideo = videoPath
    }

    fun getSelectedVideo(): String? {
        return if (selectedVideo == null)
            null
        else
            selectedVideo!!
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
        dates.forEach {
            val value = map["date"]
            if (value != null) {
                value.add(Attribute.SubAttribute("date", it, 0, true))
            } else {
                map["date"] = arrayListOf(Attribute.SubAttribute("date", it, 0, true))
            }
        }
        return ArrayList(map.map { Attribute.MainAttribute(it.key, it.value) })
    }

    sealed class CategoriesUiState {
        object Loading : CategoriesUiState()
        object Success : CategoriesUiState()
    }

    sealed class SubCategoriesUiState {
        object Loading : SubCategoriesUiState()
        object Success : SubCategoriesUiState()
    }

    sealed class AttributesUiState {
        object Loading : AttributesUiState()
        data class Success(val attributes: List<Attribute>) : AttributesUiState()
        data class Error(val message: String) : AttributesUiState()
        object NoInternetConnection : AttributesUiState()
        object EmptyView : AttributesUiState()
    }
}