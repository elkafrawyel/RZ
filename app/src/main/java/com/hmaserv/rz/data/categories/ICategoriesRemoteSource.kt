package com.hmaserv.rz.data.categories

import com.hmaserv.rz.domain.CategoryResponse
import com.hmaserv.rz.domain.DataResource

interface ICategoriesRemoteSource {
    suspend fun getCategories(): DataResource<List<CategoryResponse>>
}