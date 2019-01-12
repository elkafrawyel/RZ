package com.hmaserv.rz.domain

import com.squareup.moshi.Json


data class SubCategory(
    @field:Json(name = "files")
    val image: String?,
    @field:Json(name = "title")
    val title: String?,
    @field:Json(name = "uuid")
    val uuid: String?
)