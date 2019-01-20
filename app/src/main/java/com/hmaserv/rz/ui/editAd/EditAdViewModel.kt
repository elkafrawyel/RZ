package com.hmaserv.rz.ui.editAd

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
    val attributes = ArrayList<AttributeSection>()

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
                    val resultMap = mutableMapOf<String, ArrayList<Attribute.SubAttribute>>()
                    attributesResult.data.forEach { main ->
                        resultMap[main.name] = ArrayList(main.attributes)
                    }
                    adResult.data.mainAttributes.forEach { main ->
                        val allSubAttributes = resultMap[main.name]
                        main.attributes.forEach {sub ->
                            for (i in 0 until (allSubAttributes?.size ?: 0)) {
                                if (allSubAttributes?.get(i)?.name.equals(sub.name)) {
                                    allSubAttributes?.removeAt(i)
                                    allSubAttributes?.add(i, sub.copy(isChecked = true))
                                }
                            }
                        }
                    }

                    for ((key, value) in resultMap) {
                        attributes.add(AttributeSection(
                            true,
                            key
                        ))
                        value.forEach { sub ->
                            attributes.add(AttributeSection(
                                sub
                            ))
                        }
                    }
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
}