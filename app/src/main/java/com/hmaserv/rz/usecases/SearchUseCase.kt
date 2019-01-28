package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.ads.IAdsRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.MiniAd
import com.hmaserv.rz.domain.SearchRequest

class SearchUseCase(
    private val adsRepo: IAdsRepo
) {
    suspend fun search(subCategoryUuid: String, searchText: String)
            : DataResource<List<MiniAd>> {
        return adsRepo.search(SearchRequest(searchText, subCategoryUuid))
    }
}