package com.hmaserv.rz.service

import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.Navigation
import com.facebook.spectrum.SpectrumException
import com.facebook.spectrum.EncodedImageSink
import com.facebook.spectrum.EncodedImageSource
import com.facebook.spectrum.image.EncodedImageFormat.PNG
import com.facebook.spectrum.image.ImageSize
import com.facebook.spectrum.requirements.EncodeRequirement
import com.facebook.spectrum.options.TranscodeOptions
import com.facebook.spectrum.requirements.ResizeRequirement
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Attribute
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.ui.MainActivity
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

const val CREATE_AD_JOB_ID = 1000
const val CREATE_AD_SERVICE_NAME = "createAdJobService"
const val MODE = "mode"
const val AD_UUID = "uuid"
const val AD_TITLE = "title"
const val AD_DESCRIPTION = "description"
const val AD_PRICE = "price"
const val AD_DISCOUNT_PRICE = "discountPrice"
const val AD_QUANTITY = "quantity"
const val AD_SUB_UUID = "subUuid"
const val AD_ATTRIBUTES = "attributes"
const val AD_IMAGES = "images"
const val AD_DELETED_IMAGES = "deletedIages"
var NOTIFICATION_ID = 1000

enum class Mode {
    CREATE,
    EDIT
}

class CreateAdJobService : JobIntentService() {

    private val createAdUseCase = Injector.createAdUseCase()
    private val uploadAdImageUseCase = Injector.getUploadAdImageUseCase()

    private val resizedImages = ArrayList<File>()

    override fun onHandleWork(intent: Intent) {

        val mode = intent.getIntExtra(MODE, Mode.CREATE.ordinal)
        val adUuid = intent.getStringExtra(AD_UUID)
        val title = intent.getStringExtra(AD_TITLE)
        val description = intent.getStringExtra(AD_DESCRIPTION)
        val price = intent.getStringExtra(AD_PRICE)
        val discountPrice = intent.getStringExtra(AD_DISCOUNT_PRICE)
        val quantity = intent.getStringExtra(AD_QUANTITY)
        val subCategoryUuid = intent.getStringExtra(AD_SUB_UUID)
        val attributes = intent.getParcelableArrayListExtra<Attribute.MainAttribute>(AD_ATTRIBUTES)
        val images = intent.getStringArrayListExtra(AD_IMAGES)
        val deletedImages = intent.getStringArrayListExtra(AD_DELETED_IMAGES)
        NOTIFICATION_ID++

        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val mBuilder = NotificationCompat.Builder(this, "createAdId")
            .setSmallIcon(R.drawable.ic_fb_notification)
            .setContentTitle("RZ")
            .setContentText("Creating Ad in progress...")
            .setProgress(0, 0, true)
            .setOngoing(true)
            .setSound(uri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)


        with(NotificationManagerCompat.from(this)) {

            if (mode == Mode.CREATE.ordinal) {
                if (images != null) {
                    runBlocking(Injector.getCoroutinesDispatcherProvider().main) {
                        Toast.makeText(this@CreateAdJobService, "Creating your Ad started.", Toast.LENGTH_LONG).show()
                    }
                    notify(NOTIFICATION_ID, mBuilder.build())
                    resizeImages(images)

                    launchCreateProductTest(
                        title,
                        description,
                        price,
                        discountPrice,
                        quantity,
                        subCategoryUuid,
                        attributes,
                        this,
                        mBuilder
                    )
                }
            } else {

            }
        }

    }

    private fun resizeImages(images: java.util.ArrayList<String>) {
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
    }

    private fun launchCreateProductTest(
        title: String,
        description: String,
        price: String,
        discountPrice: String,
        quantity: String,
        subCategoryUuid: String,
        attributes: List<Attribute.MainAttribute>,
        notificationManagerCompat: NotificationManagerCompat,
        mBuilder: NotificationCompat.Builder
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
                        deleteResizedImage(image)
                    }
                    val bundle = Bundle()

                    bundle.putString("adUuid", result.data.adsUuid)
                    bundle.putString("adName", title)
                    val pendingIntent = NavDeepLinkBuilder(this@CreateAdJobService)
                        .setGraph(R.navigation.nav_graph)
                        .setComponentName(MainActivity::class.java)
                        .setDestination(R.id.adFragment)
                        .setArguments(bundle)
                        .createPendingIntent()

                    mBuilder.setProgress(0, 0, false)
                        .setContentText("Your Ad is created successfully")
                        .setContentIntent(pendingIntent)
                        .setOngoing(false)

                    withContext(Injector.getCoroutinesDispatcherProvider().main) {
                        Toast.makeText(this@CreateAdJobService, "Your Ad is created successfully.", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                is DataResource.Error -> {
                    mBuilder.setProgress(0, 0, false)
                        .setContentText("Failed to create your Ad")
                        .setOngoing(false)

                    withContext(Injector.getCoroutinesDispatcherProvider().main) {
                        Toast.makeText(this@CreateAdJobService, "Failed to create your Ad.", Toast.LENGTH_LONG).show()
                    }
                }
            }
            notificationManagerCompat.notify(NOTIFICATION_ID, mBuilder.build())
        }
    }

    private suspend fun uploadImage(adUuid: String, image: File): DataResource<Boolean> {
        return uploadAdImageUseCase.upload(adUuid, image)
    }

    private suspend fun deleteResizedImage(image: File): Boolean {
        return image.delete()
    }

    companion object {
        fun enqueueWork(
            context: Context,
            adUuid: String = "",
            title: String,
            description: String,
            price: String,
            discountPrice: String,
            quantity: String,
            subCategoryUuid: String,
            attributes: ArrayList<Attribute.MainAttribute>,
            images: ArrayList<String>,
            deletedImages: ArrayList<String> = ArrayList(),
            mode: Mode = Mode.CREATE
        ) {
            val intent = Intent(context, CreateAdJobService::class.java)
            intent.putExtra(MODE, mode.ordinal)
            intent.putExtra(AD_UUID, adUuid)
            intent.putExtra(AD_TITLE, title)
            intent.putExtra(AD_DESCRIPTION, description)
            intent.putExtra(AD_PRICE, price)
            intent.putExtra(AD_DISCOUNT_PRICE, discountPrice)
            intent.putExtra(AD_QUANTITY, quantity)
            intent.putExtra(AD_SUB_UUID, subCategoryUuid)
            intent.putParcelableArrayListExtra(AD_ATTRIBUTES, attributes)
            intent.putStringArrayListExtra(AD_IMAGES, images)
            intent.putStringArrayListExtra(AD_DELETED_IMAGES, deletedImages)
            enqueueWork(context, CreateAdJobService::class.java, CREATE_AD_JOB_ID, intent)
        }
    }

}