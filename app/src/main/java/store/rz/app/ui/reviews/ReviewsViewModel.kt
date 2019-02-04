package store.rz.app.ui.reviews

import store.rz.app.domain.DataResource
import store.rz.app.domain.Review
import store.rz.app.domain.UiState
import store.rz.app.ui.NewBaseViewModel
import store.rz.app.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val DATA_REVIEWS_KEY = "reviews"

class ReviewsViewModel : NewBaseViewModel() {

    private val reviewsUseCase = Injector.reviewsUseCase()

    var reviews = ArrayList<Review>()
    private var adUuid: String? = null

    fun setAdUuid(adUuid: String) {
        if (this.adUuid == null) {
            this.adUuid = adUuid
            getData()
        }
    }

    override fun launchDataJob(): Job {
        return scope.launch(dispatcherProvider.io) {
            withContext(dispatcherProvider.main) {
                showDataLoading()
            }
            val result = reviewsUseCase.get(adUuid!!)
            withContext(dispatcherProvider.main) {
                when (result) {
                    is DataResource.Success -> {
                        reviews.clear()
                        reviews.addAll(result.data as ArrayList<Review>)
                        showDataSuccess(reviews)
                    }
                    is DataResource.Error -> showDataError()
                }
            }
        }
    }

    private fun showDataSuccess(reviews: ArrayList<Review>) {
        _uiState.value = UiState.Success(mapOf(Pair(DATA_REVIEWS_KEY, reviews)))
    }

}