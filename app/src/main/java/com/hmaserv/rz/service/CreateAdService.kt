package com.hmaserv.rz.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.JobIntentService
import com.facebook.spectrum.SpectrumException
import com.facebook.spectrum.EncodedImageSink
import com.facebook.spectrum.EncodedImageSource
import com.facebook.spectrum.image.EncodedImageFormat.PNG
import com.facebook.spectrum.image.ImageSize
import com.facebook.spectrum.requirements.EncodeRequirement
import com.facebook.spectrum.options.TranscodeOptions
import com.facebook.spectrum.requirements.ResizeRequirement
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.MainAttribute
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException

const val CREATE_AD_SERVICE_NAME = "createAdJobService"
const val CREATE_AD_JOB_ID = 1000

class CreateAdJobService : JobIntentService() {

    private val createAdUseCase = Injector.createAdUseCase()
    private val uploadAdImageUseCase = Injector.getUploadAdImageUseCase()

    private val resizedImages = ArrayList<File>()

    override fun onHandleWork(intent: Intent) {

        val uris = intent.getStringArrayListExtra("imagesUris")

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
                                resizedImages.add(File(imagePath))
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
        launchCreateProductTest()
    }

    private fun launchCreateProductTest() {
        runBlocking {
            val result = createAdUseCase.create(
                "3f6a93ed-781b-459f-923e-9af386119690",
                "Test Android four",
                "some description",
                "1500",
                "1200",
                "100"
            )

            when (result) {
                is DataResource.Success -> {
                    for (image: File in resizedImages) {
                        uploadImage(result.data.adsUuid!!, image)
                    }
                }
                is DataResource.Error -> {

                }
            }
        }
    }

    private suspend fun uploadImage(adUuid: String, image: File) {
        uploadAdImageUseCase.upload(adUuid, image)
    }

    companion object {
        fun enqueueWork(
            context: Context,
            title: String,
            description: String,
            price: String,
            discountPrice: String,
            quantity: String,
            subCategoryUuid: String,
            attributes: List<MainAttribute>,
            images: ArrayList<String>
        ) {
            val intent = Intent(context, CreateAdJobService::class.java)
            intent.putStringArrayListExtra("imagesUris", images)
            enqueueWork(context, CreateAdJobService::class.java, CREATE_AD_JOB_ID, intent)
        }
    }

}