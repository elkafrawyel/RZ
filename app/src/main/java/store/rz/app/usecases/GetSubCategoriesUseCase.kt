package store.rz.app.usecases

import store.rz.app.data.subCategories.ISubCategoriesRepo
import store.rz.app.domain.DataResource
import store.rz.app.domain.SubCategory
import store.rz.app.domain.SubCategoryRequest

class GetSubCategoriesUseCase(
    private val subCategoriesRepo: ISubCategoriesRepo
) {

    suspend fun get(categoryUuid: String): DataResource<List<SubCategory>> {
        return subCategoriesRepo.get(SubCategoryRequest(categoryUuid))
    }

}