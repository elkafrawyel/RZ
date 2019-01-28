package com.hmaserv.rz.ui

import androidx.lifecycle.*
import com.hmaserv.rz.domain.Action
import com.hmaserv.rz.domain.Event
import com.hmaserv.rz.domain.State
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class TestViewModel<T : State, M> : ViewModel(), CoroutineScope {

    protected val dispatcherProvider = Injector.getCoroutinesDispatcherProvider()
    private val parentJob = Job()
    override val coroutineContext: CoroutineContext
        get() = dispatcherProvider.main + parentJob

    private val actionLiveData = MutableLiveData<Action>()

    private val stateLiveData = MutableLiveData<T>()
    val state : LiveData<T>
        get() = stateLiveData

    private val messagesLiveData = MutableLiveData<Event<M>>()
    val messages: LiveData<Event<M>>
        get() = messagesLiveData

    init {
        actionLiveData.observeForever(::onActionChanged)
    }

    fun sendMessage(message: M) {
        messagesLiveData.value = Event(message)
    }

    fun sendState(state: T) {
        stateLiveData.value = state
    }

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