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
    private var updateDateJob: Job? = null
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
            is Action.SelectDate -> {
                selectDate(action.position)
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
                    val attributes = result.data.mainAttributes
                        .filter { it.name != "date" }
                        .onEach { main ->
                            main.attributes[main.selectedAttribute].isChecked = true
                        }
                    val dates = result.data.mainAttributes
                        .firstOrNull { it.name == "date" }?.attributes ?: emptyList()
                    dates.firstOrNull()?.isChecked = true

                    val totalPriceDiscount = result.data.discountPrice + result.data.mainAttributes.map {
                        it.attributes.firstOrNull()?.price ?: 0
                    }.sum()

                    val totalPrice = result.data.price + result.data.mainAttributes.map {
                        it.attributes.firstOrNull()?.price ?: 0
                    }.sum()

                    sendStateOnMain {
                        State.AdState(
                            dataVisibility = View.VISIBLE,
                            updateAttribute = true,
                            ad = result.data,
                            totalPriceDiscount = totalPriceDiscount,
                            totalPrice = totalPrice,
                            attributesVisibility = if (attributes.isEmpty()) View.GONE else View.VISIBLE,
                            attributes = attributes,
                            datesVisibility = if (dates.isEmpty()) View.GONE else View.VISIBLE,
                            dates = dates
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
                    val mainAttribute = oldState.attributes[mainAttributePosition]
                    val oldSubPosition = mainAttribute.selectedAttribute
                    mainAttribute.attributes[oldSubPosition].isChecked = false
                    mainAttribute.attributes[newSubPosition].isChecked = true
                    mainAttribute.selectedAttribute = newSubPosition

                    val totalPriceDiscount = ad.discountPrice + oldState.attributes.map { main ->
                        main.attributes[main.selectedAttribute].price
                    }.sum()

                    val totalPrice = ad.price + oldState.attributes.map { main ->
                        main.attributes[main.selectedAttribute].price
                    }.sum()

                    sendStateOnMain {
                        oldState.copy(totalPrice = totalPrice, totalPriceDiscount = totalPriceDiscount)
                    }
                }
            }
        }
    }

    private fun selectDate(position: Int) {
        updateDateJob?.cancel()
        updateDateJob = launchSelectDate(position)
    }

    private fun launchSelectDate(position: Int): Job {
        return launch(dispatcherProvider.computation) {
            state.value?.let { oldState ->
                val dates = oldState.dates.mapIndexed { index, subAttribute ->
                    when (index) {
                        oldState.selectedDatePosition -> subAttribute.copy(isChecked = false)
                        position -> subAttribute.copy(isChecked = true)
                        else -> subAttribute.copy()
                    }
                }

                sendStateOnMain {
                    oldState.copy(dates = dates, selectedDatePosition = position)
                }
            }
        }
    }

    fun getSelectedAttributes(): List<Attribute.MainAttribute> {
        val attributes = ArrayList<Attribute.MainAttribute>()
        state.value?.attributes?.forEach { main ->
            val mainCopy = main.copy(attributes = main.attributes.filter { sub -> sub.isChecked })
            attributes.add(mainCopy)
        }
        val date = state.value?.dates?.filter { sub -> sub.isChecked }
        date?.let {
            if (date.isNotEmpty()) {
                attributes.add(
                    Attribute.MainAttribute(
                        date[0].mainAttributeName,
                        date
                    )
                )
            }
        }
        return attributes
    }
}