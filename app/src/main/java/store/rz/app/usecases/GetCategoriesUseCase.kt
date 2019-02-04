package store.rz.app.usecases

import store.rz.app.data.categories.ICategoriesRepo
import store.rz.app.domain.Category
import store.rz.app.domain.DataResource

class GetCategoriesUseCase(
    private val categoriesRepo: ICategoriesRepo
) {

    suspend fun get(): DataResource<List<Category>> {
        return categoriesRepo.getCategories()
    }

}