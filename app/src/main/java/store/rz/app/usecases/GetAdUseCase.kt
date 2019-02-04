package store.rz.app.usecases

import store.rz.app.data.ads.IAdsRepo
import store.rz.app.domain.Ad
import store.rz.app.domain.AdRequest
import store.rz.app.domain.DataResource

class GetAdUseCase(
    private val adsRepo: IAdsRepo
) {
    suspend fun getAd(adUuid: String): DataResource<Ad> {
        return adsRepo.getAd(AdRequest(adUuid))
    }
}