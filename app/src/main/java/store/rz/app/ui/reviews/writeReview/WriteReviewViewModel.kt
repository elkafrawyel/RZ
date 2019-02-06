package store.rz.app.ui.reviews.writeReview

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.NetworkUtils
import store.rz.app.R
import store.rz.app.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import store.rz.app.domain.*
import store.rz.app.ui.RzBaseViewModel

class WriteReviewViewModel :
    RzBaseViewModel<State.WriteReviewState, String>() {

    private var writeReviewJob: Job? = null

    private val writeReviewUseCase = Injector.writeReviewUseCase()

    var adUuid: String? = null
        set(value) {
            if (field == null) {
                field = value
            }
        }

    override fun actOnAction(action: Action) {
        when (action) {
            is Action.WriteReview -> {
                adUuid?.let { writeReview(it, action.rate, action.review) }
            }
            else -> {
            }
        }
    }

    private fun writeReview(adUuid: String, rate: Int, comment: String) {
        checkNetwork(
            job = writeReviewJob,
            success = { writeReviewJob = launchWriteJob(adUuid, rate, comment) },
            error = {
                sendMessage(Injector.getApplicationContext().getString(R.string.label_no_internet_connection))
            }
        )
    }

    private fun launchWriteJob(adUuid: String, rate: Int, comment: String): Job? {
        return launch(dispatcherProvider.io) {
            sendStateOnMain { State.WriteReviewState(onViewLoadingVisibility = View.VISIBLE) }
            val result = writeReviewUseCase.write(adUuid, rate, comment)
            when (result) {
                is DataResource.Success -> {
                    sendMessageOnMain { Injector.getApplicationContext().getString(R.string.success_send_review) }
                    sendStateOnMain { State.WriteReviewState(onViewLoadingVisibility = View.GONE, goBack = true) }
                }
                is DataResource.Error -> sendMessageOnMain {
                    Injector.getApplicationContext().getString(R.string.error_http_write_reviews_api)
                }
            }
        }
    }

}