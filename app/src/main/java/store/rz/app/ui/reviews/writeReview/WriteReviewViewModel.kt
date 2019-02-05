package store.rz.app.ui.reviews.writeReview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.NetworkUtils
import store.rz.app.R
import store.rz.app.domain.DataResource
import store.rz.app.domain.Event
import store.rz.app.domain.WriteReviewRequest
import store.rz.app.ui.BaseViewModel
import store.rz.app.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WriteReviewViewModel : BaseViewModel() {

    var count: Int = 0

    private val writeReviewUseCase = Injector.writeReviewUseCase()
    private var requestBody: WriteReviewRequest? = null

    private var writeJob: Job? = null

    protected val _writeState = MutableLiveData<Event<WriteUiState>>()

    val writeState: LiveData<Event<WriteUiState>>
        get() = _writeState

    fun writeReview(adUuid: String, rate: Int, comment: String) {
        if (NetworkUtils.isConnected()) {
            requestBody = WriteReviewRequest(adUuid = adUuid, rate = rate, comment = comment)

            if (writeJob?.isActive == true) {
                return
            }
            if (requestBody != null) {
                writeJob = launchWriteJob(requestBody!!)
            }
        } else {
            showDeleteNoInternetConnection()
        }
    }

    private fun launchWriteJob(requestBody: WriteReviewRequest): Job? {
        return scope.launch(dispatcherProvider.io) {
            withContext(dispatcherProvider.main) { showLoading() }
            val result = writeReviewUseCase.write(requestBody)
            withContext(dispatcherProvider.main) {
                when (result) {
                    is DataResource.Success -> showSuccess(result.data)
                    is DataResource.Error -> showError()
                }
            }
        }
    }

    private fun showError() {
        _writeState.value =
            Event(WriteUiState.Error(Injector.getApplicationContext().getString(R.string.error_general)))
    }

    private fun showSuccess(result: Boolean) {
        if (result) {
            _writeState.value = Event(WriteUiState.Success(result))
        } else {
            _writeState.value =
                Event(WriteUiState.Error(Injector.getApplicationContext().getString(R.string.error_http_write_reviews_api)))
        }
    }

    private fun showLoading() {
        _writeState.value = Event(WriteUiState.Loading)
    }

    private fun showDeleteNoInternetConnection() {
        _writeState.value = Event(WriteUiState.NoInternetConnection)
    }

    sealed class WriteUiState {
        object Loading : WriteUiState()
        data class Success(val sent: Boolean) : WriteUiState()
        data class Error(val message: String) : WriteUiState()
        object NoInternetConnection : WriteUiState()
    }


}