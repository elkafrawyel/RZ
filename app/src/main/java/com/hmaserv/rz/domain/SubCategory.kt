package com.hmaserv.rz.domain

import com.squareup.moshi.Json


data class SubCategory(
    @Json(name = "files")
    val image: String?,
    @Json(name = "title")
    val title: String?,
    @Json(name = "uuid")
    val uuid: String?
)