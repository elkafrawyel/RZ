package store.rz.app.data.subCategories

import store.rz.app.domain.*

interface ISubCategoriesRepo {
    suspend fun get(subCategoryRequest: SubCategoryRequest): DataResource<List<SubCategory>>
    suspend fun getSaved(): List<SubCategory>
    suspend fun getSaved(categoryUuid: String): List<SubCategory>
    suspend fun getAttributes(attributesRequest: AttributesRequest): DataResource<List<Attribute.MainAttribute>>
}