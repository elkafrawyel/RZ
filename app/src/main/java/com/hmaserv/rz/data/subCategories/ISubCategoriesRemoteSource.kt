package com.hmaserv.rz.data.subCategories

import com.hmaserv.rz.domain.*

interface ISubCategoriesRemoteSource {
    suspend fun getSubCategories(subCategoryRequest: SubCategoryRequest): DataResource<List<SubCategoryResponse>>
    suspend fun getAttributes(attributesRequest: AttributesRequest): DataResource<List<MainAttributeResponse>>
}