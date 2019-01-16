package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.logedInUser.ILoggedInUserRepo
import com.hmaserv.rz.data.uploader.IUploaderRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.utils.Constants
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class UploadAdImage(
    private val loggedInUserRepo: ILoggedInUserRepo,
    private val uploaderRepo: IUploaderRepo
) {

    private val textType = MediaType.parse("text/plain")
    private val imageType = MediaType.parse("image/*")

    suspend fun upload(adUuid: String, image: File): DataResource<Boolean> {
        val imageBody = RequestBody.create(imageType, image)
        return uploaderRepo.uploadAdImage(
            "${Constants.AUTHORIZATION_START} ${loggedInUserRepo.getLoggedInUser().token}",
            RequestBody.create(textType, adUuid),
            MultipartBody.Part.createFormData("file", image.name, imageBody)
        )
    }

}