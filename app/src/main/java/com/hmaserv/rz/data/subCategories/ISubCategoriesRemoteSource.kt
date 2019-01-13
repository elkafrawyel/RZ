package com.hmaserv.rz.data.subCategories

import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.SubCategory
import com.hmaserv.rz.domain.SubCategoryRequest

interface ISubCategoriesRemoteSource {
    suspend fun getSubCategories(subCategoryRequest: SubCategoryRequest): DataResource<List<SubCategory>>
}