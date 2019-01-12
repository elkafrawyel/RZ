package com.hmaserv.rz.framework.subCategories

import com.hmaserv.rz.data.subCategories.ISubCategoriesRemoteSource
import com.hmaserv.rz.data.subCategories.ISubCategoriesRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.SubCategory

class SubCategoriesRepo(
    private var subCategoriesRemoteSource: ISubCategoriesRemoteSource
) : ISubCategoriesRepo {

    override suspend fun getSubCategories(): DataResource<List<SubCategory>> {
        return subCategoriesRemoteSource.getSubCategories()
    }

    companion object {
        @Volatile
        private var INSTANCE: SubCategoriesRepo? = null

        fun getInstance(
            subCategoriesRemoteSource: ISubCategoriesRemoteSource
        ): SubCategoriesRepo {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SubCategoriesRepo(subCategoriesRemoteSource).also { INSTANCE = it }
            }
        }

        fun resetRemoteSource(
            categoriesRemoteSource: ISubCategoriesRemoteSource
        ) {
            INSTANCE?.subCategoriesRemoteSource = categoriesRemoteSource
        }
    }
}