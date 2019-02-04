package store.rz.app.usecases

import store.rz.app.data.categories.ICategoriesRepo
import store.rz.app.domain.Category

class GetSavedCategoriesUseCase(
    private val categoriesRepo: ICategoriesRepo
) {

    suspend fun get(): List<Category> {
        return categoriesRepo.getSavedCategories()
    }

}