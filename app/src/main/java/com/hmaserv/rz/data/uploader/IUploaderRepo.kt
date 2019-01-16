package com.hmaserv.rz.data.uploader

import com.hmaserv.rz.domain.DataResource
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface IUploaderRepo {
    suspend fun uploadAdImage(token: String, adUuid: RequestBody, image: MultipartBody.Part): DataResource<Boolean>
}