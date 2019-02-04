package store.rz.app.framework.categories

import store.rz.app.data.categories.ICategoriesLocalSource
import store.rz.app.data.categories.ICategoriesRemoteSource
import store.rz.app.data.categories.ICategoriesRepo
import store.rz.app.domain.Category
import store.rz.app.domain.DataResource
import store.rz.app.domain.toCategory

class CategoriesRepo(
    private var categoriesRemoteSource: ICategoriesRemoteSource,
    private val categoriesLocalSource: ICategoriesLocalSource
) : ICategoriesRepo {

    override suspend fun getCategories(): DataResource<List<Category>> {
        val result = categoriesRemoteSource.getCategories()
        return when (result) {
            is DataResource.Success -> {
                val data = result.data.mapNotNull { it.toCategory() }
                categoriesLocalSource.deleteAll()
                categoriesLocalSource.save(data)
                DataResource.Success(data)
            }
            is DataResource.Error -> result
        }
    }

    override suspend fun getSavedCategories(): List<Category> {
        return categoriesLocalSource.getAll()
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