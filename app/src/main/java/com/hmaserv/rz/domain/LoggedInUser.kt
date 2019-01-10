package com.hmaserv.rz.domain

import com.squareup.moshi.Json
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class LoggedInUser(
    @Id var id: Long = 0,
    val avatar: String?,
    val email: String?,
    val fullName: String?,
    val mobile: String?,
    val role: String?,
    val status: String?,
    val token: String?,
    val uuid: String?
)

data class LogInUserRequest(
    val phone: String,
    val password: String
)

data class RegisterUserRequest(
    @Json(name = "full_name") val fullName: String,
    @Json(name = "mobile") val phone: String,
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

data class ForgetPassword(
    val email: String
)