package store.rz.app.usecases

import store.rz.app.data.logedInUser.ILoggedInUserRepo
import store.rz.app.data.uploader.IUploaderRepo
import store.rz.app.domain.DataResource
import store.rz.app.utils.Constants
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