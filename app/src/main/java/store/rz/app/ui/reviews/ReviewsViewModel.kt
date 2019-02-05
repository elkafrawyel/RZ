package store.rz.app.ui.reviews

import android.view.View
import store.rz.app.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import store.rz.app.R
import store.rz.app.domain.*
import store.rz.app.ui.RzBaseViewModel

class ReviewsViewModel :
    RzBaseViewModel<State.ReviewsState, String>() {

    private var reviewsJob: Job? = null
    private val reviewsUseCase = Injector.reviewsUseCase()

    private var adUuid: String? = null

    fun setAdUuid(adUuid: String) {
        if (this.adUuid == null) {
            this.adUuid = adUuid
            sendAction(Action.Started)
        }
    }

    override fun actOnAction(action: Action) {
        when (action) {
            Action.Started -> {
                refreshData(false)
            }
            Action.Refresh -> {
                refreshData(true)
            }
            else -> {
            }
        }
    }

    private fun refreshData(isRefreshed: Boolean) {
        checkNetwork(
            job = reviewsJob,
            success = { reviewsJob = launchReviewsJob(isRefreshed) },
            error = {
                if (isRefreshed) {
                    sendMessage(Injector.getApplicationContext().getString(R.string.label_no_internet_connection))
                    sendState(
                        State.ReviewsState(
                            isRefreshing = false,
                            dataVisibility = View.VISIBLE,
                            reviews = state.value?.reviews ?: emptyList()
                        )
                    )
                } else {
                    sendState(State.ReviewsState(noConnectionVisibility = View.VISIBLE))
                }
            }
        )
    }

    private fun launchReviewsJob(isRefreshed: Boolean): Job {
        return launch(dispatcherProvider.io) {
            sendStateOnMain {
                if (isRefreshed) {
                    State.ReviewsState(
                        isRefreshing = true,
                        dataVisibility = View.VISIBLE,
                        reviews = state.value?.reviews ?: emptyList()
                    )
                } else {
                    State.ReviewsState(loadingVisibility = View.VISIBLE)
                }
            }
            val result = reviewsUseCase.get(adUuid!!)
            when (result) {
                is DataResource.Success -> {
                    if (result.data.isEmpty()) {
                        sendStateOnMain { State.ReviewsState(emptyVisibility = View.VISIBLE) }
                    } else {
                        sendStateOnMain {
                            State.ReviewsState(
                                dataVisibility = View.VISIBLE,
                                reviews = result.data
                            )
                        }
                    }
                }
                is DataResource.Error -> sendStateOnMain { State.ReviewsState(errorVisibility = View.VISIBLE) }
            }
        }
    }

}