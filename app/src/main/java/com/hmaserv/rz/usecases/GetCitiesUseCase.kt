package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.lookups.ILookUpsRepo
import com.hmaserv.rz.domain.City
import com.hmaserv.rz.domain.DataResource

class GetCitiesUseCase(
    private val lookUpsRepo: ILookUpsRepo
) {

    suspend fun getCities(): DataResource<List<City>> {
        return lookUpsRepo.getCities()
    }

}