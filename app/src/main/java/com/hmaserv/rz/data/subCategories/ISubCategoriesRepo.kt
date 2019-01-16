package com.hmaserv.rz.data.subCategories

import com.hmaserv.rz.domain.*

interface ISubCategoriesRepo {
    suspend fun get(subCategoryRequest: SubCategoryRequest): DataResource<List<SubCategory>>
    suspend fun getSaved(): List<SubCategory>
    suspend fun getSaved(categoryUuid: String): List<SubCategory>
    suspend fun getAttributes(attributesRequest: AttributesRequest): DataResource<List<Attribute.MainAttribute>>
}