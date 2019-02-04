package store.rz.app.usecases

import store.rz.app.data.logedInUser.ILoggedInUserRepo
import store.rz.app.data.uploader.IUploaderRepo
import store.rz.app.domain.DataResource
import store.rz.app.utils.Constants
import okhttp3.MediaType
import okhttp3.RequestBody

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