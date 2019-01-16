package com.hmaserv.rz.data.categories

import com.hmaserv.rz.domain.Category

interface ICategoriesLocalSource {
    suspend fun getAll(): List<Category>
    suspend fun save(categories: List<Category>): Boolean
    suspend fun deleteAll(): Boolean
}