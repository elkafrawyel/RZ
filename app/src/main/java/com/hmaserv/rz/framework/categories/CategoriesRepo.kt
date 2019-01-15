package com.hmaserv.rz.framework.categories

import com.hmaserv.rz.data.categories.ICategoriesLocalSource
import com.hmaserv.rz.data.categories.ICategoriesRemoteSource
import com.hmaserv.rz.data.categories.ICategoriesRepo
import com.hmaserv.rz.domain.Category
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.toCategory

class CategoriesRepo(
    private var categoriesRemoteSource: ICategoriesRemoteSource,
    private val categoriesLocalSource: ICategoriesLocalSource
) : ICategoriesRepo {

    override suspend fun getCategories(): DataResource<List<Category>> {
        val result = categoriesRemoteSource.getCategories()
        return when (result) {
            is DataResource.Success -> {
                val data = result.data.mapNotNull { it.toCategory() }
                categoriesLocalSource.deleteCategories()
                categoriesLocalSource.saveCategories(data)
                DataResource.Success(data)
            }
            is DataResource.Error -> result
        }
    }

    override suspend fun getSavedCategories(): List<Category> {
        return categoriesLocalSource.getCategories()
    }

    companion object {
        @Volatile
        private var INSTANCE: CategoriesRepo? = null

        fun getInstance(
            categoriesRemoteSource: ICategoriesRemoteSource,
            categoriesLocalSource: ICategoriesLocalSource
        ): CategoriesRepo {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CategoriesRepo(categoriesRemoteSource, categoriesLocalSource).also { INSTANCE = it }
            }
        }

        fun resetRemoteSource(
            categoriesRemoteSource: ICategoriesRemoteSource
        ) {
            INSTANCE?.categoriesRemoteSource = categoriesRemoteSource
        }
    }
}