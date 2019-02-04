package store.rz.app.usecases

import store.rz.app.data.subCategories.ISubCategoriesRepo
import store.rz.app.domain.SubCategory

class GetSavedSubCategoriesUseCase(
    private val subCategoriesRepo: ISubCategoriesRepo
) {

    suspend fun get(categoryUuid: String): List<SubCategory> {
        return subCategoriesRepo.getSaved(categoryUuid)
    }

}