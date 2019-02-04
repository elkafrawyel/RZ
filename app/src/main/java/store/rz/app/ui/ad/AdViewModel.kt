package store.rz.app.ui.ad

import android.view.View
import store.rz.app.domain.*
import store.rz.app.ui.RzBaseViewModel
import store.rz.app.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AdViewModel : RzBaseViewModel<State.AdState, String>() {

    var adUuid: String? = null
    private var adJob: Job? = null
    private var updateAttributeJob: Job? = null
    private val getAdUseCase = Injector.getAdUseCase()

    fun setAdId(adUuid: String) {
        if (this.adUuid == null) {
            this.adUuid = adUuid
            sendAction(Action.Started)
        }
    }

    override fun actOnAction(action: Action) {
        when (action) {
            Action.Started -> {
                refreshData()
            }
            is Action.SelectAttribute -> {
                selectAttribute(action.mainAttributePosition, action.subAttributePosition)
            }
            else -> {
            }
        }
    }

    private fun refreshData() {
        checkNetwork(
            job = adJob,
            success = { adJob = launchAdJob() },
            error = { sendState(State.AdState(noConnectionVisibility = View.VISIBLE)) }
        )
    }

    private fun launchAdJob(): Job {
        return launch(dispatcherProvider.io) {
            sendStateOnMain { State.AdState(loadingVisibility = View.VISIBLE) }
            val result = getAdUseCase.getAd(adUuid!!)
            when (result) {
                is DataResource.Success -> {
                    result.data.mainAttributes.onEach { main ->
                        main.attributes[main.selectedAttribute].isChecked = true
                    }
                    val totalPrice = result.data.discountPrice + result.data.mainAttributes.map {
                        it.attributes.firstOrNull()?.price ?: 0
                    }.sum()
                    sendStateOnMain {
                        State.AdState(
                            dataVisibility = View.VISIBLE,
                            updateAttribute = true,
                            ad = result.data,
                            totalPrice = totalPrice
                        )
                    }
                }
                is DataResource.Error -> sendStateOnMain { State.AdState(errorVisibility = View.VISIBLE) }
            }
        }
    }

    private fun selectAttribute(mainAttributePosition: Int, newSubPosition: Int) {
        updateAttributeJob?.cancel()
        updateAttributeJob = launchSelectAttribute(mainAttributePosition, newSubPosition)
    }

    private fun launchSelectAttribute(mainAttributePosition: Int, newSubPosition: Int): Job {
        return launch(dispatcherProvider.computation) {
            state.value?.let { oldState ->
                oldState.ad?.let { ad ->
                    val mainAttribute = ad.mainAttributes[mainAttributePosition]
                    val oldSubPosition = mainAttribute.selectedAttribute
                    mainAttribute.attributes[oldSubPosition].isChecked = false
                    mainAttribute.attributes[newSubPosition].isChecked = true
                    mainAttribute.selectedAttribute = newSubPosition

                    val totalPrice = ad.discountPrice + ad.mainAttributes.map { main ->
                        main.attributes[main.selectedAttribute].price
                    }.sum()
                    sendStateOnMain {
                        State.AdState(
                            dataVisibility = View.VISIBLE,
                            ad = ad,
                            totalPrice = totalPrice
                        )
                    }
                }
            }
        }
    }

    fun getSelectedAttributes(): List<Attribute.MainAttribute> {
        val attributes = ArrayList<Attribute.MainAttribute>()
        state.value?.ad?.mainAttributes?.forEach { main ->
            val mainCopy = main.copy(attributes = main.attributes.filter { sub -> sub.isChecked })
            attributes.add(mainCopy)
        }
        return attributes
    }
}