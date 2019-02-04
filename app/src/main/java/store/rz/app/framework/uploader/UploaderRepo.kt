package store.rz.app.framework.uploader

import store.rz.app.data.uploader.IUploaderRemoteSource
import store.rz.app.data.uploader.IUploaderRepo
import store.rz.app.domain.DataResource
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploaderRepo(
    private var uploaderRemoteSource: IUploaderRemoteSource
): IUploaderRepo {

    override suspend fun uploadAdImage(
        token: String,
        adUuid: RequestBody,
        image: MultipartBody.Part
    ): DataResource<Boolean> {
        return uploaderRemoteSource.uploadAdImage(token, adUuid, image)
    }

    override suspend fun deleteAdImage(
        token: String,
        adUuid: RequestBody,
        imagePath: RequestBody,
        flag: RequestBody
    ): DataResource<Boolean> {
        return uploaderRemoteSource.deleteAdImage(token, adUuid, imagePath, flag)
    }

    companion object {
        @Volatile
        private var INSTANCE: UploaderRepo? = null

        fun getInstance(
            uploaderRemoteSource: IUploaderRemoteSource
        ): UploaderRepo {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UploaderRepo(uploaderRemoteSource).also { INSTANCE = it }
            }
        }

        fun resetRemoteSource(
            uploaderRemoteSource: IUploaderRemoteSource
        ) {
            INSTANCE?.uploaderRemoteSource = uploaderRemoteSource
        }
    }

}