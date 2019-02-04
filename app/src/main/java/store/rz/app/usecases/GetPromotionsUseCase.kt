package store.rz.app.usecases

import store.rz.app.data.ads.IAdsRepo
import store.rz.app.domain.DataResource
import store.rz.app.domain.MiniAd

class GetPromotionsUseCase(
    private val adsRepo: IAdsRepo
) {
    suspend fun get():DataResource<List<MiniAd>>{
        return adsRepo.getPromotions()
    }
}