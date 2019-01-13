package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.subCategories.ISubCategoriesRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.SubCategory
import com.hmaserv.rz.domain.SubCategoryRequest

class GetSubCategoriesUseCase(
    private val subCategoriesRepo: ISubCategoriesRepo
) {

    suspend fun get(categoryUuid: String): DataResource<List<SubCategory>> {
        return subCategoriesRepo.getSubCategories(SubCategoryRequest(categoryUuid))
    }

}