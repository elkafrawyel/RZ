package store.rz.app.usecases

import store.rz.app.data.subCategories.ISubCategoriesRepo
import store.rz.app.domain.Attribute
import store.rz.app.domain.AttributesRequest
import store.rz.app.domain.DataResource

class GetAttributesUseCase(
    private val subCategoriesRepo: ISubCategoriesRepo
) {

    suspend fun get(subCategoryUuid: String): DataResource<List<Attribute.MainAttribute>> {
        return subCategoriesRepo.getAttributes(AttributesRequest(subCategoryUuid))
    }

}