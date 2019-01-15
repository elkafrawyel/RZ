package com.hmaserv.rz.service

import android.app.IntentService
import android.content.Intent
import android.net.Uri
import com.facebook.spectrum.SpectrumException
import com.facebook.spectrum.EncodedImageSink
import com.facebook.spectrum.EncodedImageSource
import com.facebook.spectrum.image.EncodedImageFormat.PNG
import com.facebook.spectrum.image.ImageSize
import com.facebook.spectrum.requirements.EncodeRequirement
import com.facebook.spectrum.options.TranscodeOptions
import com.facebook.spectrum.requirements.ResizeRequirement
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

const val CREATE_AD_SERVICE_NAME = "createAdService"

class CreateAdService : IntentService(CREATE_AD_SERVICE_NAME) {

    private val dispatcherProvider = Injector.getCoroutinesDispatcherProvider()
    private val parentJob = Job()
    private val scope = CoroutineScope(parentJob)

    private var createProductJob: Job? = null
    private val createProductUseCase = Injector.getCreateProductUseCase()

    private val resizedImages = ArrayList<String>()

    override fun onHandleIntent(intent: Intent?) {

        val uris = intent?.getStringArrayListExtra("imagesUris")

        if (uris != null) {
            for (uri: String in uris) {
                try {
                    contentResolver.openInputStream(Uri.parse(uri)).use { inputStream ->
                        val transcodeOptions = TranscodeOptions.Builder(EncodeRequirement(PNG, 80))
                            .resize(ResizeRequirement.Mode.EXACT_OR_SMALLER, ImageSize(1080, 1080))
                            .build()

                        val imagePath = Injector.getNewResizedImagePath()
                        if (imagePath != null) {
                            val result = Injector.getSpectrum().transcode(
                                EncodedImageSource.from(inputStream),
                                EncodedImageSink.from(imagePath),
                                transcodeOptions,
                                CREATE_AD_SERVICE_NAME
                            )
                            if (result.isSuccessful)
                                resizedImages.add(imagePath)
                        }

                    }
                } catch (e: IOException) {
                    // e.g. file not found
                    e.printStackTrace()
                } catch (e: SpectrumException) {
                    // e.g. invalid input image
                    e.printStackTrace()
                }
            }

            createProductTest()
        }

    }

    fun createProductTest() {
        if (createProductJob?.isActive == true) {
            return
        }

        createProductJob = launchCreateProductTest()
    }

    private fun launchCreateProductTest(): Job {
        return scope.launch {
            val result = createProductUseCase.create(
                "3f6a93ed-781b-459f-923e-9af386119690",
                "Test Android two",
                "some description",
                "1500",
                "1200",
                "100"
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        parentJob.cancel()
    }

}