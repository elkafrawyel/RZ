package com.hmaserv.rz.framework.uploader

import com.hmaserv.rz.data.uploader.IUploaderRemoteSource
import com.hmaserv.rz.data.uploader.IUploaderRepo
import com.hmaserv.rz.domain.DataResource
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