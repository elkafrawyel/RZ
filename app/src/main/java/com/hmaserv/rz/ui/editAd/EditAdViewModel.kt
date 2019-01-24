package com.hmaserv.rz.ui.editAd

import android.content.ClipData
import android.net.Uri
import com.hmaserv.rz.domain.*
import com.hmaserv.rz.ui.NewBaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val DATA_AD_KEY = "ad"

class EditAdViewModel : NewBaseViewModel() {

    private val getAdUseCase = Injector.getAdUseCase()
    private val getAttributesUseCase = Injector.getAttributesUseCase()

    private var adUuid: String? = null
    val images = ArrayList<Image>(10)
    private val deletedImages = ArrayList<Image.UrlImage>(10)
    val attributes = ArrayList<AttributeSection>()

    lateinit var currentAd: Ad

    fun setAdUuid(adUuid: String) {
        if (this.adUuid == null) {
            this.adUuid = adUuid
            getData()
        }
    }

    override fun launchDataJob(): Job {
        return scope.launch(dispatcherProvider.io) {
            withContext(dispatcherProvider.main) { showDataLoading() }
            if (adUuid != null) {
                val adResult = getAdUseCase.getAd(adUuid!!)
                val attributesResult = getAttributesUseCase.get("3f6a93ed-781b-459f-923e-9af386119690")
                if (adResult is DataResource.Success && attributesResult is DataResource.Success) {
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
            } else {
                withContext(dispatcherProvider.main) { showDataError() }
            }
        }
    }

    private fun showSuccess(data: Ad) {
        _uiState.value = UiState.Success(mapOf(Pair(DATA_AD_KEY, data)))
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