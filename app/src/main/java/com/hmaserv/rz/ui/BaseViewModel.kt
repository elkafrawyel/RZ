package com.hmaserv.rz.ui

import androidx.lifecycle.ViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

open class BaseViewModel : ViewModel() {
    protected val dispatcherProvider = Injector.getCoroutinesDispatcherProvider()
    private val parentJob = Job()
    protected val scope = CoroutineScope(dispatcherProvider.main + parentJob)

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}