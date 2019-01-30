package com.hmaserv.rz.utils

import android.os.Environment
import android.util.Log
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

const val RZ_DCIM_DIR_NAME = "rz"
const val RZ_CAPTURED_IMG_EXT = ".jpg"

private fun generateImageName(isResized: Boolean = false): String {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyyMMDD_HHmmss_SSS", Locale.ENGLISH)
    val name = dateFormat.format(calendar.time) ?: Random.nextInt().toString()
    return if (isResized) "RESIZED_$name" else "RZ_IMG_$name"
}

fun getImageTempFile(): File {
    val imageName = generateImageName()
    val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
    val rzImagesDir = File(storageDir, RZ_DCIM_DIR_NAME)
    if (!rzImagesDir.exists()) {
        if (!rzImagesDir.mkdirs()) {
            Timber.e("Directory not created")
        }
    }

    return File.createTempFile(imageName, RZ_CAPTURED_IMG_EXT, rzImagesDir)
}