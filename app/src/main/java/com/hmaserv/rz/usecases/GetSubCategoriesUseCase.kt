package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.subCategories.ISubCategoriesRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.SubCategory

class GetSubCategoriesUseCase(
    private val subCategoriesRepo: ISubCategoriesRepo
) {

    suspend fun get(): DataResource<List<SubCategory>> {
        return subCategoriesRepo.getSubCategories()
    }

}