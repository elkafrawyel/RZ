package com.hmaserv.rz.domain
import com.squareup.moshi.Json


data class ApiResponse<T>(
    @field:Json(name = "data")
    val data: T?,
    @field:Json(name = "message")
    val message: String?,
    @field:Json(name = "success")
    val success: Boolean?,
    @field:Json(name = "meta")
    val meta: Meta?
)

data class Meta(
    @Json(name = "pagination")
    val pagination: Pagination?
)

data class Pagination(
    @Json(name = "count")
    val count: Int?,
    @Json(name = "current_page")
    val currentPage: Int?,
    @Json(name = "links")
    val links: List<Any?>?,
    @Json(name = "per_page")
    val perPage: Int?,
    @Json(name = "total")
    val total: Int?,
    @Json(name = "total_pages")
    val totalPages: Int?
)