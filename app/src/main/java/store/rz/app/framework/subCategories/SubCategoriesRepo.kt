package store.rz.app.framework.subCategories

import store.rz.app.data.subCategories.ISubCategoriesLocalSource
import store.rz.app.data.subCategories.ISubCategoriesRemoteSource
import store.rz.app.data.subCategories.ISubCategoriesRepo
import store.rz.app.domain.*

class SubCategoriesRepo(
    private var subCategoriesLocalSource: ISubCategoriesLocalSource,
    private var subCategoriesRemoteSource: ISubCategoriesRemoteSource
) : ISubCategoriesRepo {

    override suspend fun get(subCategoryRequest: SubCategoryRequest): DataResource<List<SubCategory>> {
        val result = subCategoriesRemoteSource.getSubCategories(subCategoryRequest)
        return when (result) {
            is DataResource.Success -> {
                val data = result.data.mapNotNull { it.toSubCategory(subCategoryRequest.categoryUuid) }
                subCategoriesLocalSource.delete(subCategoryRequest.categoryUuid)
                subCategoriesLocalSource.save(data)
                DataResource.Success(data)
            }
            is DataResource.Error -> result
        }
    }

    override suspend fun getSaved(): List<SubCategory> {
        return subCategoriesLocalSource.getAll()
    }

    override suspend fun getSaved(categoryUuid: String): List<SubCategory> {
        return subCategoriesLocalSource.get(categoryUuid)
    }

    override suspend fun getAttributes(attributesRequest: AttributesRequest): DataResource<List<Attribute.MainAttribute>> {
        val result = subCategoriesRemoteSource.getAttributes(attributesRequest)
        return when (result) {
            is DataResource.Success -> {
                val data = result.data.mapNotNull { it.toMainAttribute() }
                DataResource.Success(data)
            }
            is DataResource.Error -> result
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SubCategoriesRepo? = null

        fun getInstance(
            subCategoriesLocalSource: ISubCategoriesLocalSource,
            subCategoriesRemoteSource: ISubCategoriesRemoteSource
        ): SubCategoriesRepo {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SubCategoriesRepo(subCategoriesLocalSource, subCategoriesRemoteSource).also { INSTANCE = it }
            }
        }

        fun resetRemoteSource(
            categoriesRemoteSource: ISubCategoriesRemoteSource
        ) {
            INSTANCE?.subCategoriesRemoteSource = categoriesRemoteSource
        }
    }
}