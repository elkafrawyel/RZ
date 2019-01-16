package com.hmaserv.rz.framework.uploader

import com.hmaserv.rz.data.apiService.RetrofitAuthApiService
import com.hmaserv.rz.data.uploader.IUploaderRemoteSource
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.utils.safeApiCall
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.IOException

class UploaderRemoteSource(
    private val authApiService: RetrofitAuthApiService
) : IUploaderRemoteSource {

    override suspend fun uploadAdImage(
        token: String,
        adUuid: RequestBody,
        image: MultipartBody.Part
    ): DataResource<Boolean> {
        return safeApiCall(
            call = { uploadAdImageCall(token, adUuid, image) },
            errorMessage = "Error uploading ad image"
        )
    }

    private suspend fun uploadAdImageCall(
        token: String,
        adUuid: RequestBody,
        image: MultipartBody.Part
    ): DataResource<Boolean> {
        val response = authApiService.upload(
            token,
            adUuid,
            image
        ).await()
        if (response.success != null && response.success) {
            return DataResource.Success(true)
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException("Error uploading ad image"))
    }

}