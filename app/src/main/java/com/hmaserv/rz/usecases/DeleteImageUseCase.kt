package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.logedInUser.ILoggedInUserRepo
import com.hmaserv.rz.data.uploader.IUploaderRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.utils.Constants
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

private const val UPLOADER_DELETE_FLAG = "1"

class DeleteImageUseCase(
    val loggedInUserRepo: ILoggedInUserRepo,
    private val uploaderRepo: IUploaderRepo
) {

    private val textType = MediaType.parse("text/plain")

    suspend fun delete(adUuid: String, imagePath: String): DataResource<Boolean> {
        return uploaderRepo.deleteAdImage(
            "${Constants.AUTHORIZATION_START} ${loggedInUserRepo.getLoggedInUser().token}",
            RequestBody.create(textType, adUuid),
            RequestBody.create(textType, imagePath.substring(imagePath.indexOf("file"))),
            RequestBody.create(textType, UPLOADER_DELETE_FLAG)
        )
    }

}