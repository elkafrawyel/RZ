package com.hmaserv.rz.domain

import com.squareup.moshi.Json


data class Product(
    @Json(name = "ads_uuid")
    val uuid: String?,
    @Json(name = "files")
    val image: String?,
    @Json(name = "price")
    val price: Int?,
    @Json(name = "rate")
    val rate: Int?,
    @Json(name = "sub_category")
    val subCategory: String?,
    @Json(name = "title")
    val title: String?
)