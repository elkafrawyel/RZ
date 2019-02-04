package store.rz.app.framework.subCategories

import store.rz.app.data.subCategories.ISubCategoriesLocalSource
import store.rz.app.domain.SubCategory
import store.rz.app.domain.SubCategory_
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query

class SubCategoriesLocalSource(
    boxStore: BoxStore
): ISubCategoriesLocalSource {

    private val subCategoriesBox: Box<SubCategory> = boxStore.boxFor()

    override suspend fun get(categoryUuid: String): List<SubCategory> {
        return subCategoriesBox.query {
            equal(SubCategory_.categoryUuid, categoryUuid)
        }.find().filterNotNull()
    }

    override suspend fun getAll(): List<SubCategory> {
        return subCategoriesBox.all?.filterNotNull() ?: listOf()
    }

    override suspend fun save(subCategories: List<SubCategory>): Boolean {
        subCategoriesBox.put(subCategories)
        return true
    }

    override suspend fun delete(categoryUuid: String): Boolean {
        subCategoriesBox.query {
            equal(SubCategory_.categoryUuid, categoryUuid)
        }.remove()
        return true
    }

    override suspend fun deleteAll(): Boolean {
        subCategoriesBox.removeAll()
        return true
    }

}