package com.hmaserv.rz.framework.categories

import com.hmaserv.rz.data.categories.ICategoriesLocalSource
import com.hmaserv.rz.domain.Category
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor

class CategoriesLocalSource(
    boxStore: BoxStore
): ICategoriesLocalSource {

    private val categoriesBox: Box<Category> = boxStore.boxFor()

    override suspend fun getCategories(): List<Category> {
        return categoriesBox.all?.filterNotNull() ?: listOf()
    }

    override suspend fun saveCategories(categories: List<Category>): Boolean {
        categoriesBox.put(categories)
        return true
    }

    override suspend fun deleteCategories(): Boolean {
        categoriesBox.removeAll()
        return true
    }

}