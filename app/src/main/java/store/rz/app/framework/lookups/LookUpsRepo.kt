package store.rz.app.framework.lookups

import store.rz.app.data.lookups.ILookUpsRemoteSource
import store.rz.app.data.lookups.ILookUpsRepo
import store.rz.app.domain.City
import store.rz.app.domain.DataResource
import store.rz.app.domain.toCity

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