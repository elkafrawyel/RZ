package com.hmaserv.rz.ui.createProduct

import android.content.ClipData
import android.net.Uri
import com.hmaserv.rz.ui.BaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CreateProductViewModel : BaseViewModel() {

    private var createProductJob: Job? = null
    private val selectedImagesList = ArrayList<Uri>(10)

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

    fun addSelectedImage(uri: Uri): Boolean {
        if (selectedImagesList.size < 10) {
            selectedImagesList.add(uri)
            return true
        }

        return false
    }

    fun addSelectedImages(clipData: ClipData): Boolean {
        if (selectedImagesList.size + clipData.itemCount < 11) {
            for (i in 0 until clipData.itemCount) {
                selectedImagesList.add(clipData.getItemAt(i).uri)
            }
            return true
        }

        return false
    }

    fun getSelectedImagesList(): ArrayList<Uri> {
        return selectedImagesList
    }

    fun getSelectedImagesStringList(): ArrayList<String> {
        return ArrayList(selectedImagesList.map { it.toString() })
    }

    fun getSelectedImagesSize(): Int {
        return selectedImagesList.size
    }

    fun removeUri(position: Int) {
        selectedImagesList.removeAt(position)
    }
}