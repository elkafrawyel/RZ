package com.hmaserv.rz.ui.reviews

import com.hmaserv.rz.domain.UiState
import com.hmaserv.rz.ui.NewBaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val DATA_REVIEWS_KEY = "reviews"

class ReviewsViewModel : NewBaseViewModel() {

//    private val reviewsUseCase = Injector.getReviewsUseCase()

    var reviews = ArrayList<String>()
    private var adUuid: String? = null

    fun setAdUuid(adUuid: String) {
        this.adUuid = adUuid
        getData()
    }

    override fun launchDataJob(): Job {
        return scope.launch(dispatcherProvider.io) {
            withContext(dispatcherProvider.main) {
                showDataLoading()
            }

            reviews.add("- Reviews")
            withContext(dispatcherProvider.main) {
                showDataSuccess(reviews)
            }
        }
    }

    private fun showDataSuccess(reviews: ArrayList<String>) {
        _uiState.value = UiState.Success(mapOf(Pair(DATA_REVIEWS_KEY, reviews)))
    }


}