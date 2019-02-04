package store.rz.app.data.categories

import store.rz.app.domain.CategoryResponse
import store.rz.app.domain.DataResource

interface ICategoriesRemoteSource {
    suspend fun getCategories(): DataResource<List<CategoryResponse>>
}