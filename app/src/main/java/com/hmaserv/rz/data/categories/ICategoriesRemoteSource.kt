package com.hmaserv.rz.data.categories

import com.hmaserv.rz.domain.Category
import com.hmaserv.rz.domain.DataResource

interface ICategoriesRemoteSource {
    suspend fun getCategories(): DataResource<List<Category>>
}