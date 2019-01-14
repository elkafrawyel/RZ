package com.hmaserv.rz.domain

import com.squareup.moshi.Json


data class SubCategory(
    @field:Json(name = "images")
    val images: List<String?>?,
    @field:Json(name = "title")
    val title: String?,
    @field:Json(name = "uuid")
    val uuid: String?
)

data class SubCategoryRequest(
    @field:Json(name = "cat_uuid") val categoryUuid: String
)