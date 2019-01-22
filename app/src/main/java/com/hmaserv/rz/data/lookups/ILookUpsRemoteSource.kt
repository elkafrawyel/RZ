package com.hmaserv.rz.data.lookups

import com.hmaserv.rz.domain.CityResponse
import com.hmaserv.rz.domain.DataResource

interface ILookUpsRemoteSource {
    suspend fun getCities(): DataResource<List<CityResponse>>
}