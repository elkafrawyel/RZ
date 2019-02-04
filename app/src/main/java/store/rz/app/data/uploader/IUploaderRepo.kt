package store.rz.app.data.uploader

import store.rz.app.domain.DataResource
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface IUploaderRepo {
    suspend fun uploadAdImage(token: String, adUuid: RequestBody, image: MultipartBody.Part): DataResource<Boolean>
    suspend fun deleteAdImage(token: String, adUuid: RequestBody, imagePath: RequestBody, flag: RequestBody): DataResource<Boolean>
}