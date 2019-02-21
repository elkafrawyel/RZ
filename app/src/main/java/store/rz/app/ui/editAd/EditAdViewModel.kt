package store.rz.app.ui.editAd

import android.content.ClipData
import android.net.Uri
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

const val DATA_AD_KEY = "ad"

class EditAdViewModel :
    RzBaseViewModel<State.EditAdState, String>() {

    private var dataJob: Job? = null

    protected val _uiState = MutableLiveData<UiState>()
    val uiState : LiveData<UiState>
        get() = _uiState

    private val getAdUseCase = Injector.getAdUseCase()
    private val getAttributesUseCase = Injector.getAttributesUseCase()

    private var adUuid: String? = null
    val images = ArrayList<Image>(10)
    private val deletedImages = ArrayList<Image.UrlImage>(10)
    val attributes = ArrayList<AttributeSection>()

    lateinit var currentAd: Ad

    override fun actOnAction(action: Action) {

    }

    fun setAdUuid(adUuid: String) {
        if (this.adUuid == null) {
            this.adUuid = adUuid
            getData()
        }
    }

    protected fun getData() {
        if (NetworkUtils.isConnected()) {
            if (dataJob?.isActive == true) {
                return
            }

            dataJob = launchDataJob()
        } else {
            showNoInternetConnection()
        }
    }

    fun launchDataJob(): Job {
        return launch(dispatcherProvider.io) {
            withContext(dispatcherProvider.main) { showDataLoading() }
            if (adUuid != null) {
                val adResult = getAdUseCase.getAd(adUuid!!)
                if (adResult is DataResource.Success) {
                    val attributesResult = getAttributesUseCase.get(adResult.data.subCategoryUuid)
                    if (attributesResult is DataResource.Success) {
                        images.addAll(adResult.data.images)

                        val resultMap = mutableMapOf<String, ArrayList<Attribute.SubAttribute>>()
                        var dateIndex = -1
                        attributesResult.data.forEach { main ->
                            resultMap[main.name] = ArrayList(main.attributes)
                        }
                        adResult.data.mainAttributes.forEach { main ->
                            if (main.name == "date") dateIndex = adResult.data.mainAttributes.indexOf(main)
                            val allSubAttributes = resultMap[main.name]
                            main.attributes.forEach { sub ->
                                for (i in 0 until (allSubAttributes?.size ?: 0)) {
                                    if (allSubAttributes?.get(i)?.name.equals(sub.name)) {
                                        allSubAttributes?.removeAt(i)
                                        allSubAttributes?.add(i, sub.copy(isChecked = true))
                                    }
                                }
                            }
                        }

                        for ((key, value) in resultMap) {
                            attributes.add(
                                AttributeSection(
                                    true,
                                    key
                                )
                            )
                            value.forEach { sub ->
                                attributes.add(
                                    AttributeSection(
                                        sub
                                    )
                                )
                            }
                        }

                        if (dateIndex != -1) {
                            attributes.add(
                                AttributeSection(
                                    true,
                                    "date"
                                )
                            )
                            adResult.data.mainAttributes[dateIndex].attributes.forEach { sub ->
                                attributes.add(
                                    AttributeSection(
                                        sub.copy(isChecked = true)
                                    )
                                )
                            }
                        }

                        currentAd = adResult.data
                        withContext(dispatcherProvider.main) { showSuccess(adResult.data) }
                    } else {
                        withContext(dispatcherProvider.main) { showDataError() }
                    }
                }

            } else {
                withContext(dispatcherProvider.main) { showDataError() }
            }
        }
    }

    private fun showSuccess(data: Ad) {
        _uiState.value = UiState.Success(mapOf(Pair(DATA_AD_KEY, data)))
    }

    private fun showDataLoading() {
        _uiState.value = UiState.Loading
    }

    private fun showDataError() {
        _uiState.value = UiState.Error(Injector.getApplicationContext().getString(R.string.error_general))
    }

    private fun showNoInternetConnection() {
        _uiState.value = UiState.NoInternetConnection
    }

    fun refresh() {
        getData()
    }

    fun addSelectedImage(uri: Uri): Boolean {
        if (images.size < 10) {
            images.add(Image.UriImage(uri))
            return true
        }

        return false
    }

    fun addSelectedImages(clipData: ClipData): Boolean {
        if (images.size + clipData.itemCount < 11) {
            for (i in 0 until clipData.itemCount) {
                images.add(Image.UriImage(clipData.getItemAt(i).uri))
            }
            return true
        }

        return false
    }

    fun getNewImages(): ArrayList<String> {
        return ArrayList(
            images
                .filter { it is Image.UriImage }
                .map { (it as Image.UriImage).uri.toString() }
        )
    }

    fun getDeletedImages(): ArrayList<String> {
        return ArrayList(deletedImages.map { it.url })
    }

    fun removeImage(position: Int) {
        val image = images[position]
        if (image is Image.UrlImage) {
            deletedImages.add(image)
        }
        images.removeAt(position)
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
}