package com.hmaserv.rz.data.subCategories

import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.SubCategory
import com.hmaserv.rz.domain.SubCategoryRequest

interface ISubCategoriesRepo {
    suspend fun get(subCategoryRequest: SubCategoryRequest): DataResource<List<SubCategory>>
    suspend fun getSaved(): List<SubCategory>
    suspend fun getSaved(categoryUuid: String): List<SubCategory>
}