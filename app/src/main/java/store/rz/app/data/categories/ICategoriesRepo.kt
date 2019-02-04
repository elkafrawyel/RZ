package store.rz.app.data.categories

import store.rz.app.domain.Category
import store.rz.app.domain.DataResource

interface ICategoriesRepo {
    suspend fun getCategories(): DataResource<List<Category>>
    suspend fun getSavedCategories(): List<Category>
}