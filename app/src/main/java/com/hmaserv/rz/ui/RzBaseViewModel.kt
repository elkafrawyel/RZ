package com.hmaserv.rz.ui

import androidx.annotation.MainThread
import androidx.lifecycle.*
import com.blankj.utilcode.util.NetworkUtils
import com.hmaserv.rz.domain.Action
import com.hmaserv.rz.domain.Event
import com.hmaserv.rz.domain.State
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

abstract class RzBaseViewModel<T : State, M> : ViewModel(), CoroutineScope {

    protected val dispatcherProvider = Injector.getCoroutinesDispatcherProvider()
    private val parentJob = Job()
    override val coroutineContext: CoroutineContext
        get() = dispatcherProvider.main + parentJob

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
        parentJob.cancel()
        actionLiveData.removeObserver(::onActionChanged)
    }
}