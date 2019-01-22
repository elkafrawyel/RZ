package com.hmaserv.rz.framework.lookups

import com.hmaserv.rz.data.lookups.ILookUpsRemoteSource
import com.hmaserv.rz.data.lookups.ILookUpsRepo
import com.hmaserv.rz.domain.City
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.toCity

class LookUpsRepo(
    private var lookUpsRemoteSource: ILookUpsRemoteSource
) : ILookUpsRepo {

    override suspend fun getCities(): DataResource<List<City>> {
        val result = lookUpsRemoteSource.getCities()
        return when (result) {
            is DataResource.Success -> {
                val data = result.data.mapNotNull { it.toCity() }
                DataResource.Success(data)
            }
            is DataResource.Error -> result
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: LookUpsRepo? = null

        fun getInstance(
            lookUpsRemoteSource: ILookUpsRemoteSource
        ): LookUpsRepo {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LookUpsRepo(lookUpsRemoteSource).also { INSTANCE = it }
            }
        }

        fun resetRemoteSource(
            lookUpsRemoteSource: ILookUpsRemoteSource
        ) {
            INSTANCE?.lookUpsRemoteSource = lookUpsRemoteSource
        }
    }

}