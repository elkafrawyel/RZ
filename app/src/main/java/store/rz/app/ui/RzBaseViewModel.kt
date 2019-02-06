package store.rz.app.ui

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.NetworkUtils
import store.rz.app.domain.Action
import store.rz.app.domain.Event
import store.rz.app.domain.State
import store.rz.app.utils.Injector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

abstract class RzBaseViewModel<T : State, M> : ViewModel(), CoroutineScope {

    protected val dispatcherProvider = Injector.getCoroutinesDispatcherProvider()
    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext

    private val actionLiveData = MutableLiveData<Action>()

    private val stateLiveData = MutableLiveData<T>()
    val state: LiveData<T>
        get() = stateLiveData

    private val messagesLiveData = MutableLiveData<Event<M>>()
    val messages: LiveData<Event<M>>
        get() = messagesLiveData

    init {
        actionLiveData.observeForever(::onActionChanged)
    }

    protected fun checkNetwork(job: Job?, success: () -> Unit, error: () -> Unit) {
        if (NetworkUtils.isConnected()) {
            if (job?.isActive == true) {
                return
            }
            success.invoke()
        } else {
            error.invoke()
        }
    }

    @MainThread
    protected fun sendMessage(message: M) {
        messagesLiveData.value = Event(message)
    }

    protected suspend fun sendMessageOnMain(block: () -> M) =
        withContext(dispatcherProvider.main) { sendMessage(block.invoke()) }

    @MainThread
    protected fun sendState(state: T) {
        stateLiveData.value = state
    }

    protected suspend fun sendStateOnMain(block: () -> T) =
        withContext(dispatcherProvider.main) { sendState(block.invoke()) }

    @MainThread
    fun sendAction(action: Action) {
        actionLiveData.value = action
    }

    private fun onActionChanged(action: Action?) {
        action?.let(::actOnAction)
    }

    protected abstract fun actOnAction(action: Action)

    override fun onCleared() {
        super.onCleared()
        actionLiveData.removeObserver(::onActionChanged)
    }
}