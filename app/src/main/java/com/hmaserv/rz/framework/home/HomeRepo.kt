package com.hmaserv.rz.framework.home

import com.hmaserv.rz.data.home.IHomeRemoteSource
import com.hmaserv.rz.data.home.IHomeRepo
import com.hmaserv.rz.domain.*

class HomeRepo(
    private var homeRemoteSource: IHomeRemoteSource
) : IHomeRepo {

    override suspend fun getSlider(): DataResource<List<Slider>> {
        return homeRemoteSource.getSlider()
    }

    override suspend fun getPromotions(): DataResource<List<MiniAd>> {
        val result = homeRemoteSource.getPromotions()
        return when(result) {
            is DataResource.Success -> DataResource.Success(result.data.mapNotNull { it.toMiniProduct() })
            is DataResource.Error -> result
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: HomeRepo? = null

        fun getInstance(
            homeRemoteSource: IHomeRemoteSource
        ): HomeRepo {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HomeRepo(homeRemoteSource).also { INSTANCE = it }
            }
        }

        fun resetRemoteSource(
            homeRemoteSource: IHomeRemoteSource
        ) {
            INSTANCE?.homeRemoteSource = homeRemoteSource
        }
    }

}