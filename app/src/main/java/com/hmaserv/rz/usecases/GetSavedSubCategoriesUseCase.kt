package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.subCategories.ISubCategoriesRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.SubCategory
import com.hmaserv.rz.domain.SubCategoryRequest

class GetSavedSubCategoriesUseCase(
    private val subCategoriesRepo: ISubCategoriesRepo
) {

    suspend fun get(categoryUuid: String): List<SubCategory> {
        return subCategoriesRepo.getSaved(categoryUuid)
    }

}