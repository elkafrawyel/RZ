package com.hmaserv.rz.domain

import com.squareup.moshi.Json


data class Product(
    @field:Json(name = "ads_uuid")
    val uuid: String?,
    @field:Json(name = "files")
    val images: List<String?>?,
    @field:Json(name = "price")
    val price: Int?,
    @field:Json(name = "rate")
    val rate: Int?,
    @field:Json(name = "sub_category")
    val subCategory: String?,
    @field:Json(name = "title")
    val title: String?
)

data class ProductRequest(
    @field:Json(name = "sub_cat_uuid")
    val subCategoryUuid: String
)