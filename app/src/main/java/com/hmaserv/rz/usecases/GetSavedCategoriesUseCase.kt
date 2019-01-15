package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.categories.ICategoriesRepo
import com.hmaserv.rz.domain.Category
import com.hmaserv.rz.domain.DataResource

class GetSavedCategoriesUseCase(
    private val categoriesRepo: ICategoriesRepo
) {

    suspend fun get(): List<Category> {
        return categoriesRepo.getSavedCategories()
    }

}