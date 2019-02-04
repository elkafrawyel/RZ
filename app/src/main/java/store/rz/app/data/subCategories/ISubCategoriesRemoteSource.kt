package store.rz.app.data.subCategories

import store.rz.app.domain.*

interface ISubCategoriesRemoteSource {
    suspend fun getSubCategories(subCategoryRequest: SubCategoryRequest): DataResource<List<SubCategoryResponse>>
    suspend fun getAttributes(attributesRequest: AttributesRequest): DataResource<List<MainAttributeResponse>>
}