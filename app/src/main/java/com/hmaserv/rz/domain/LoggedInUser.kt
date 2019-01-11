package com.hmaserv.rz.domain

import com.hmaserv.rz.utils.Constants
import com.squareup.moshi.Json
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class LoggedInUser(
    @Id(assignable = true) var id: Long = Constants.LOGGED_IN_USER_ID,
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
    @field:Json(name = "mobile") val phone: String,
    val password: String
)

data class RegisterUserRequest(
    @field:Json(name = "full_name") val fullName: String,
    @field:Json(name = "mobile") val phone: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "password") val password: String
)

data class ForgetPassword(
    val email: String
)