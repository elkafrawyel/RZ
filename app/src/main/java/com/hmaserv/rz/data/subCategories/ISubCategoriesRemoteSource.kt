package com.hmaserv.rz.data.subCategories

import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.SubCategoryRequest
import com.hmaserv.rz.domain.SubCategoryResponse

interface ISubCategoriesRemoteSource {
    suspend fun getSubCategories(subCategoryRequest: SubCategoryRequest): DataResource<List<SubCategoryResponse>>
}