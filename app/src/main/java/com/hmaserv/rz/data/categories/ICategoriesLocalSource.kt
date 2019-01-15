package com.hmaserv.rz.data.categories

import com.hmaserv.rz.domain.Category

interface ICategoriesLocalSource {
    suspend fun getCategories(): List<Category>
    suspend fun saveCategories(categories: List<Category>): Boolean
    suspend fun deleteCategories(): Boolean
}