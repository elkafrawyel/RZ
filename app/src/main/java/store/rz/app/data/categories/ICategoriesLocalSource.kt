package store.rz.app.data.categories

import store.rz.app.domain.Category

interface ICategoriesLocalSource {
    suspend fun getAll(): List<Category>
    suspend fun save(categories: List<Category>): Boolean
    suspend fun deleteAll(): Boolean
}