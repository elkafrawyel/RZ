package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.subCategories.ISubCategoriesRepo
import com.hmaserv.rz.domain.Attribute
import com.hmaserv.rz.domain.AttributesRequest
import com.hmaserv.rz.domain.DataResource

class GetAttributesUseCase(
    private val subCategoriesRepo: ISubCategoriesRepo
) {

    suspend fun get(subCategoryUuid: String): DataResource<List<Attribute.MainAttribute>> {
        return subCategoriesRepo.getAttributes(AttributesRequest(subCategoryUuid))
    }

}