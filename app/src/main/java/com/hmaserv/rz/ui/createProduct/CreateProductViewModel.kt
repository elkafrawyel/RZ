package com.hmaserv.rz.ui.createProduct

import android.util.Log
import androidx.lifecycle.ViewModel
import com.hmaserv.rz.ui.BaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CreateProductViewModel : BaseViewModel() {

    private var createProductJob: Job? = null

    private val createProductUseCase = Injector.getCreateProductUseCase()

    fun createProductTest() {
        if (createProductJob?.isActive == true) {
            return
        }

        createProductJob = launchCreateProductTest()
    }

    private fun launchCreateProductTest(): Job {
        return scope.launch(dispatcherProvider.io) {
            val result = createProductUseCase.create(
                "3f6a93ed-781b-459f-923e-9af386119690",
                "Test Android one",
                "some description",
                "1500",
                "1200",
                "100"
            )
        }
    }
}