package com.hmaserv.rz.framework.uploader

import com.hmaserv.rz.R
import com.hmaserv.rz.data.apiService.RetrofitAuthApiService
import com.hmaserv.rz.data.uploader.IUploaderRemoteSource
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.utils.Injector
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
            errorMessage = Injector.getApplicationContext().getString(R.string.error_uploading_image)
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

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_uploading_image)))
    }

    override suspend fun deleteAdImage(
        token: String,
        adUuid: RequestBody,
        imagePath: RequestBody,
        flag: RequestBody
    ): DataResource<Boolean> {
        return safeApiCall(
            call = { deleteAdImageCall(token, adUuid, imagePath, flag) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_uploading_image)
        )
    }

    private suspend fun deleteAdImageCall(
        token: String,
        adUuid: RequestBody,
        imagePath: RequestBody,
        flag: RequestBody
    ): DataResource<Boolean> {
        val response = authApiService.deleteImage(
            token,
            adUuid,
            imagePath,
            flag
        ).await()
        if (response.success != null && response.success) {
            return DataResource.Success(true)
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_uploading_image)))
    }

}