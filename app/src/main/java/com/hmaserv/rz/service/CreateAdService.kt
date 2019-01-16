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

const val CREATE_AD_JOB_ID = 1000
const val CREATE_AD_SERVICE_NAME = "createAdJobService"
const val AD_TITLE = "createAdJobService"
const val AD_DESCRIPTION = "createAdJobService"
const val AD_PRICE = "createAdJobService"
const val AD_DISCOUNT_PRICE = "createAdJobService"
const val AD_QUANTITY = "createAdJobService"
const val AD_SUB_UUID = "createAdJobService"
const val AD_ATTRIBUTES = "createAdJobService"
const val AD_IMAGES = "images"

class CreateAdJobService : JobIntentService() {

    private val createAdUseCase = Injector.createAdUseCase()
    private val uploadAdImageUseCase = Injector.getUploadAdImageUseCase()

    private val resizedImages = ArrayList<File>()

    override fun onHandleWork(intent: Intent) {

        val title = intent.getStringExtra(AD_TITLE)
        val description = intent.getStringExtra(AD_DESCRIPTION)
        val price = intent.getStringExtra(AD_PRICE)
        val discountPrice = intent.getStringExtra(AD_DISCOUNT_PRICE)
        val quantity = intent.getStringExtra(AD_QUANTITY)
        val subCategoryUuid = intent.getStringExtra(AD_SUB_UUID)
//        val attributes = intent.getParcelableArrayListExtra<MainAttribute>(AD_ATTRIBUTES)
        val images = intent.getStringArrayListExtra(AD_IMAGES)

        if (images != null) {
            for (uri: String in images) {
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

            launchCreateProductTest(
                title,
                description,
                price,
                discountPrice,
                quantity,
                subCategoryUuid,
                listOf()
            )
        }

    }

    private fun launchCreateProductTest(
        title: String,
        description: String,
        price: String,
        discountPrice: String,
        quantity: String,
        subCategoryUuid: String,
        attributes: List<MainAttribute>
    ) {
        runBlocking {
            val result = createAdUseCase.create(
                subCategoryUuid,
                title,
                description,
                price,
                discountPrice,
                quantity,
                attributes
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