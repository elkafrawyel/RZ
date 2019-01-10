package com.hmaserv.rz.domain

data class ApiResponse<T>(
    val `data`: T?,
    val message: String?,
    val success: Boolean?
)