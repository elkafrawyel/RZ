package com.hmaserv.rz.ui.Ad

import com.hmaserv.rz.domain.Ad
import com.hmaserv.rz.domain.Attribute
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.UiState
import com.hmaserv.rz.ui.NewBaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val DATA_AD_DETAILS = "ad_details"

class AdViewModel : NewBaseViewModel() {

    private val getAdUseCase = Injector.getAdUseCase()

    private var adUuid: String? = null

    val attributes = ArrayList<Attribute.MainAttribute>()
    val selectedAttributes = ArrayList<Attribute.MainAttribute>()

    fun setAdId(adUuid: String) {
        if (this.adUuid == null) {
            this.adUuid = adUuid
            getData()
        }
    }

    override fun launchDataJob(): Job {
        return scope.launch(dispatcherProvider.computation) {
            withContext(dispatcherProvider.main) { showDataLoading() }
            if (adUuid != null) {
                val result = getAdUseCase.getAd(adUuid!!)
                withContext(dispatcherProvider.main) {
                    when (result) {
                        is DataResource.Success -> {
                            result.data.mainAttributes.forEach { main ->
                                main.attributes.firstOrNull()?.isChecked = true
                                selectedAttributes.add(main.copy(attributes = main.attributes.take(1)))
                            }
                            attributes.addAll(result.data.mainAttributes)
                            showSuccess(result.data)
                        }
                        is DataResource.Error -> showDataError()
                    }
                }
            }else{
                showDataError()
            }
        }
    }

    fun getAttributesPrice(): Int {
        var price = 0
        selectedAttributes.forEach {
            price += it.attributes.firstOrNull()?.price ?: 0
        }

        return price
    }

    private fun showSuccess(ad: Ad) {
        _uiState.value = UiState.Success(mapOf(Pair(DATA_AD_DETAILS, ad)))
    }
}