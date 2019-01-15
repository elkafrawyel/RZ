package com.hmaserv.rz.service

import android.app.IntentService
import android.content.Intent
import com.facebook.spectrum.SpectrumException
import com.facebook.spectrum.EncodedImageSink
import com.facebook.spectrum.EncodedImageSource
import com.facebook.spectrum.image.EncodedImageFormat.JPEG
import com.facebook.spectrum.image.ImageSize
import com.facebook.spectrum.requirements.EncodeRequirement
import com.facebook.spectrum.options.TranscodeOptions
import com.facebook.spectrum.requirements.ResizeRequirement
import java.io.IOException


const val CREATE_AD_SERVICE_NAME = "createAdService"

class CreateAdService : IntentService(CREATE_AD_SERVICE_NAME) {

    override fun onHandleIntent(intent: Intent?) {
//        try {
//            contentResolver.openInputStream(intent?.data).use { inputStream ->
//                val transcodeOptions = TranscodeOptions.Builder(EncodeRequirement(JPEG, 80))
//                    .resize(ResizeRequirement.Mode.EXACT_OR_SMALLER, ImageSize(2048, 2048))
//                    .build()
//
//                val result = mSpectrum.transcode(
//                    EncodedImageSource.from(inputStream),
//                    EncodedImageSink.from("my/output/file/path/upload.jpeg"),
//                    transcodeOptions,
//                    "upload_flow_callsite_identifier"
//                )
//            }
//        } catch (e: IOException) {
//            // e.g. file not found
//        } catch (e: SpectrumException) {
//            // e.g. invalid input image
//        }

    }

}