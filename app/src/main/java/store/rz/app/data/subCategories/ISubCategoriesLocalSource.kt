package store.rz.app.data.subCategories

import store.rz.app.domain.SubCategory

interface ISubCategoriesLocalSource {
    suspend fun get(categoryUuid: String): List<SubCategory>
    suspend fun getAll(): List<SubCategory>
    suspend fun save(subCategories: List<SubCategory>): Boolean
    suspend fun delete(categoryUuid: String): Boolean
    suspend fun deleteAll(): Boolean
}