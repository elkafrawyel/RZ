package store.rz.app.ui

import androidx.lifecycle.ViewModel
import store.rz.app.utils.Injector
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