package store.rz.app.usecases

import store.rz.app.data.ads.IAdsRepo
import store.rz.app.domain.DataResource
import store.rz.app.domain.MiniAd
import store.rz.app.domain.SearchRequest

class SearchUseCase(
    private val adsRepo: IAdsRepo
) {
    suspend fun search(subCategoryUuid: String, searchText: String)
            : DataResource<List<MiniAd>> {
        return adsRepo.search(SearchRequest(searchText, subCategoryUuid))
    }
}