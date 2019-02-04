package store.rz.app.framework.categories

import store.rz.app.data.categories.ICategoriesLocalSource
import store.rz.app.domain.Category
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor

class CategoriesLocalSource(
    boxStore: BoxStore
): ICategoriesLocalSource {

    private val categoriesBox: Box<Category> = boxStore.boxFor()

    override suspend fun getAll(): List<Category> {
        return categoriesBox.all?.filterNotNull() ?: listOf()
    }

    override suspend fun save(categories: List<Category>): Boolean {
        categoriesBox.put(categories)
        return true
    }

    override suspend fun deleteAll(): Boolean {
        categoriesBox.removeAll()
        return true
    }

}