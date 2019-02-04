package store.rz.app.data.lookups

import store.rz.app.domain.City
import store.rz.app.domain.DataResource

interface ILookUpsRepo {
    suspend fun getCities(): DataResource<List<City>>
}