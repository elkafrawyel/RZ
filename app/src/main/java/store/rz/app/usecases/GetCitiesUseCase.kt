package store.rz.app.usecases

import store.rz.app.data.lookups.ILookUpsRepo
import store.rz.app.domain.City
import store.rz.app.domain.DataResource

class GetCitiesUseCase(
    private val lookUpsRepo: ILookUpsRepo
) {

    suspend fun getCities(): DataResource<List<City>> {
        return lookUpsRepo.getCities()
    }

}