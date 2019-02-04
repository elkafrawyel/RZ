package store.rz.app.data.lookups

import store.rz.app.domain.CityResponse
import store.rz.app.domain.DataResource

interface ILookUpsRemoteSource {
    suspend fun getCities(): DataResource<List<CityResponse>>
}