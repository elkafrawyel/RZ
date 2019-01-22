package com.hmaserv.rz.data.lookups

import com.hmaserv.rz.domain.City
import com.hmaserv.rz.domain.DataResource

interface ILookUpsRepo {
    suspend fun getCities(): DataResource<List<City>>
}