package com.hmaserv.rz.framework.categories

import com.hmaserv.rz.data.categories.ICategoriesRemoteSource
import com.hmaserv.rz.data.categories.ICategoriesRepo
import com.hmaserv.rz.domain.Category
import com.hmaserv.rz.domain.DataResource

class CategoriesRepo(
    private var categoriesRemoteSource: ICategoriesRemoteSource
) : ICategoriesRepo {

    override suspend fun getCategories(): DataResource<List<Category>> {
        return categoriesRemoteSource.getCategories()
    }

    companion object {
        @Volatile
        private var INSTANCE: CategoriesRepo? = null

        fun getInstance(
            categoriesRemoteSource: ICategoriesRemoteSource
        ): CategoriesRepo {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CategoriesRepo(categoriesRemoteSource).also { INSTANCE = it }
            }
        }

        fun resetRemoteSource(
            categoriesRemoteSource: ICategoriesRemoteSource
        ) {
            INSTANCE?.categoriesRemoteSource = categoriesRemoteSource
        }
    }
}