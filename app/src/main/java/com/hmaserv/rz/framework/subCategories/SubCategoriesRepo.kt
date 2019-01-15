package com.hmaserv.rz.framework.subCategories

import com.hmaserv.rz.data.subCategories.ISubCategoriesLocalSource
import com.hmaserv.rz.data.subCategories.ISubCategoriesRemoteSource
import com.hmaserv.rz.data.subCategories.ISubCategoriesRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.SubCategory
import com.hmaserv.rz.domain.SubCategoryRequest
import com.hmaserv.rz.domain.toSubCategory

class SubCategoriesRepo(
    private var subCategoriesLocalSource: ISubCategoriesLocalSource,
    private var subCategoriesRemoteSource: ISubCategoriesRemoteSource
) : ISubCategoriesRepo {

    override suspend fun get(subCategoryRequest: SubCategoryRequest): DataResource<List<SubCategory>> {
        val result = subCategoriesRemoteSource.getSubCategories(subCategoryRequest)
        return when (result) {
            is DataResource.Success -> {
                val data = result.data.mapNotNull { it.toSubCategory(subCategoryRequest.categoryUuid) }
                subCategoriesLocalSource.delete(subCategoryRequest.categoryUuid)
                subCategoriesLocalSource.save(data)
                DataResource.Success(data)
            }
            is DataResource.Error -> result
        }
    }

    override suspend fun getSaved(): List<SubCategory> {
        return subCategoriesLocalSource.getAll()
    }

    override suspend fun getSaved(categoryUuid: String): List<SubCategory> {
        return subCategoriesLocalSource.get(categoryUuid)
    }

    companion object {
        @Volatile
        private var INSTANCE: SubCategoriesRepo? = null

        fun getInstance(
            subCategoriesLocalSource: ISubCategoriesLocalSource,
            subCategoriesRemoteSource: ISubCategoriesRemoteSource
        ): SubCategoriesRepo {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SubCategoriesRepo(subCategoriesLocalSource, subCategoriesRemoteSource).also { INSTANCE = it }
            }
        }

        fun resetRemoteSource(
            categoriesRemoteSource: ISubCategoriesRemoteSource
        ) {
            INSTANCE?.subCategoriesRemoteSource = categoriesRemoteSource
        }
    }
}