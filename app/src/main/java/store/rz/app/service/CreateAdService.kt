package store.rz.app.service

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
import com.facebook.spectrum.SpectrumException
import com.facebook.spectrum.EncodedImageSink
import com.facebook.spectrum.EncodedImageSource
import com.facebook.spectrum.image.EncodedImageFormat.PNG
import com.facebook.spectrum.image.ImageSize
import com.facebook.spectrum.requirements.EncodeRequirement
import com.facebook.spectrum.options.TranscodeOptions
import com.facebook.spectrum.requirements.ResizeRequirement
import store.rz.app.R
import store.rz.app.domain.Attribute
import store.rz.app.domain.DataResource
import store.rz.app.ui.MainActivity
import store.rz.app.utils.Constants
import store.rz.app.utils.Injector
import store.rz.app.utils.changeLanguage
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
const val AD_VIDEO = "video"

var CREATE_NOTIFICATION_ID = 1000
var MODIFY_NOTIFICATION_ID = 2000

enum class Mode {
    CREATE,
    UPDATE
}

class CreateAdJobService : JobIntentService() {

    private val createAdUseCase = Injector.createAdUseCase()
    private val updateAdUseCase = Injector.updateAdUseCase()
    private val uploadAdImageUseCase = Injector.getUploadAdImageUseCase()
    private val deleteImageUseCase = Injector.deleteAdImageUseCase()

    private val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    override fun onCreate() {
        super.onCreate()
        changeLanguage(Constants.Language.DEFAULT)
    }

    override fun onHandleWork(intent: Intent) {
        val mode = intent.getIntExtra(MODE, Mode.CREATE.ordinal)
        if (mode == Mode.CREATE.ordinal) {
            createAd(intent)
        } else {
            updateAd(intent)
        }
    }

    private fun createAd(
        intent: Intent
    ) {
        val title = intent.getStringExtra(AD_TITLE)
        val description = intent.getStringExtra(AD_DESCRIPTION)
        val price = intent.getStringExtra(AD_PRICE)
        val discountPrice = intent.getStringExtra(AD_DISCOUNT_PRICE)
        val quantity = intent.getStringExtra(AD_QUANTITY)
        val subCategoryUuid = intent.getStringExtra(AD_SUB_UUID)
        val attributes = intent.getParcelableArrayListExtra<Attribute.MainAttribute>(AD_ATTRIBUTES)
        val images = intent.getStringArrayListExtra(AD_IMAGES)
        val videoUri = intent.getStringExtra(AD_VIDEO)

        CREATE_NOTIFICATION_ID += 1
        val mBuilder = NotificationCompat.Builder(this, Constants.NOTIFICATION_CREATE_AD_CHANNEL)
            .setSmallIcon(R.drawable.ic_fb_notification)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.status_create_ad))
            .setProgress(0, 0, true)
            .setOngoing(true)
            .setSound(soundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(this)) {

            if (images != null) {
                runBlocking(Injector.getCoroutinesDispatcherProvider().main) {
                    Toast.makeText(this@CreateAdJobService, getString(R.string.status_create_ad), Toast.LENGTH_LONG)
                        .show()
                }
                notify(CREATE_NOTIFICATION_ID, mBuilder.build())
                val resizedImages = resizeImages(images)

                val videoFile = File(videoUri)

                launchCreateAd(
                    title,
                    description,
                    price,
                    discountPrice,
                    quantity,
                    subCategoryUuid,
                    attributes,
                    resizedImages,
                    videoFile,
                    CREATE_NOTIFICATION_ID,
                    this,
                    mBuilder
                )
            }
        }
    }

    private fun launchCreateAd(
        title: String,
        description: String,
        price: String,
        discountPrice: String,
        quantity: String,
        subCategoryUuid: String,
        attributes: List<Attribute.MainAttribute>,
        resizedImages: ArrayList<File>,
        video: File,
        notificationId: Int,
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

                    uploadImage(result.data.adsUuid!!, video)

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
                        .setContentText(getString(R.string.success_create_ad))
                        .setContentIntent(pendingIntent)
                        .setOngoing(false)
                        .setAutoCancel(true)

                    withContext(Injector.getCoroutinesDispatcherProvider().main) {
                        Toast.makeText(
                            this@CreateAdJobService,
                            getString(R.string.success_create_ad),
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
                is DataResource.Error -> {
                    mBuilder.setProgress(0, 0, false)
                        .setContentText(getString(R.string.error_create_ad))
                        .setOngoing(false)
                        .setAutoCancel(true)

                    withContext(Injector.getCoroutinesDispatcherProvider().main) {
                        Toast.makeText(this@CreateAdJobService, getString(R.string.error_create_ad), Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
            notificationManagerCompat.notify(notificationId, mBuilder.build())
        }
    }

    private fun updateAd(
        intent: Intent
    ) {
        val adUuid = intent.getStringExtra(AD_UUID)
        val title = intent.getStringExtra(AD_TITLE)
        val description = intent.getStringExtra(AD_DESCRIPTION)
        val price = intent.getStringExtra(AD_PRICE)
        val discountPrice = intent.getStringExtra(AD_DISCOUNT_PRICE)
        val quantity = intent.getStringExtra(AD_QUANTITY)
        val subCategoryUuid = intent.getStringExtra(AD_SUB_UUID)
        val attributes = intent.getParcelableArrayListExtra<Attribute.MainAttribute>(AD_ATTRIBUTES)
        val deletedImages = intent.getStringArrayListExtra(AD_DELETED_IMAGES)
        val images = intent.getStringArrayListExtra(AD_IMAGES)

        MODIFY_NOTIFICATION_ID += 1

        val mBuilder = NotificationCompat.Builder(this, Constants.NOTIFICATION_EDIT_AD_CHANNEL)
            .setSmallIcon(R.drawable.ic_fb_notification)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.status_edit_ad))
            .setProgress(0, 0, true)
            .setOngoing(true)
            .setSound(soundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(this)) {

            if (images != null) {
                runBlocking(Injector.getCoroutinesDispatcherProvider().main) {
                    Toast.makeText(this@CreateAdJobService, getString(R.string.status_edit_ad), Toast.LENGTH_LONG)
                        .show()
                }
                notify(MODIFY_NOTIFICATION_ID, mBuilder.build())
                val resizedImages = resizeImages(images)

                launchUpdateAd(
                    adUuid,
                    title,
                    description,
                    price,
                    discountPrice,
                    quantity,
                    subCategoryUuid,
                    attributes,
                    deletedImages,
                    resizedImages,
                    MODIFY_NOTIFICATION_ID,
                    this,
                    mBuilder
                )
            }
        }
    }

    private fun launchUpdateAd(
        adUuid: String,
        title: String,
        description: String,
        price: String,
        discountPrice: String,
        quantity: String,
        subCategoryUuid: String,
        attributes: List<Attribute.MainAttribute>,
        deletedImages: ArrayList<String>,
        resizedImages: ArrayList<File>,
        notificationId: Int,
        notificationManagerCompat: NotificationManagerCompat,
        mBuilder: NotificationCompat.Builder
    ) {
        runBlocking {
            val result = updateAdUseCase.update(
                adUuid,
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

                    for (imagePath: String in deletedImages) {
                        deleteImage(adUuid, imagePath)
                    }

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
                        .setContentText(getString(R.string.success_edit_ad))
                        .setContentIntent(pendingIntent)
                        .setOngoing(false)
                        .setAutoCancel(true)

                    withContext(Injector.getCoroutinesDispatcherProvider().main) {
                        Toast.makeText(this@CreateAdJobService, getString(R.string.success_edit_ad), Toast.LENGTH_LONG)
                            .show()
                    }
                }
                is DataResource.Error -> {
                    mBuilder.setProgress(0, 0, false)
                        .setContentText(getString(R.string.error_edit_ad))
                        .setOngoing(false)
                        .setAutoCancel(true)

                    withContext(Injector.getCoroutinesDispatcherProvider().main) {
                        Toast.makeText(this@CreateAdJobService, getString(R.string.error_edit_ad), Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
            notificationManagerCompat.notify(notificationId, mBuilder.build())
        }
    }

    private fun resizeImages(
        images: ArrayList<String>
    ): ArrayList<File> {

        val resizedImages = ArrayList<File>()
        for (uri: String in images) {
            try {
                contentResolver.openInputStream(Uri.parse(uri)).use { inputStream ->
                    val transcodeOptions = TranscodeOptions.Builder(EncodeRequirement(PNG, 80))
                        .resize(ResizeRequirement.Mode.EXACT_OR_SMALLER, ImageSize(720, 720))
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

        return resizedImages
    }

    private suspend fun uploadImage(
        adUuid: String,
        image: File
    ): DataResource<Boolean> {
        return uploadAdImageUseCase.upload(adUuid, image)
    }

    private suspend fun deleteImage(
        adUuid: String,
        imagePath: String
    ): DataResource<Boolean> {
        return deleteImageUseCase.delete(adUuid, imagePath)
    }

    private fun deleteResizedImage(
        image: File
    ): Boolean {
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
            mode: Mode = Mode.CREATE,
            video: String = ""
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
            intent.putExtra(AD_VIDEO, video)
            enqueueWork(context, CreateAdJobService::class.java, CREATE_AD_JOB_ID, intent)
        }
    }

}