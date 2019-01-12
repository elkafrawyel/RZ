package com.hmaserv.rz.data.subCategories

import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.SubCategory

interface ISubCategoriesRemoteSource {
    suspend fun getSubCategories(): DataResource<List<SubCategory>>
}