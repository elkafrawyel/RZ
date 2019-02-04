package store.rz.app.usecases

import store.rz.app.data.ads.IAdsRepo
import store.rz.app.domain.DataResource
import store.rz.app.domain.MiniAd
import store.rz.app.domain.MiniAdRequest

class GetMiniAdsUseCase(
    private val adsRepo: IAdsRepo
) {

    suspend fun get(subCategoryUuid: String): DataResource<List<MiniAd>> {
        return adsRepo.getMiniAds(MiniAdRequest(subCategoryUuid))
    }

}