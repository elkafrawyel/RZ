package store.rz.app.usecases

import store.rz.app.data.ads.IAdsRepo
import store.rz.app.domain.DataResource
import store.rz.app.domain.Slider

class GetSliderUseCase(
    private val adsRepo: IAdsRepo
) {
    suspend fun get(): DataResource<List<Slider>> {
        return adsRepo.getSlider()
    }
}