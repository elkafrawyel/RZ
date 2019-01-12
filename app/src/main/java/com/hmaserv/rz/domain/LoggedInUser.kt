package com.hmaserv.rz.domain

import com.hmaserv.rz.utils.Constants
import com.squareup.moshi.Json
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class LoggedInUser(
    @Id(assignable = true) var id: Long = Constants.LOGGED_IN_USER_ID,
    @field:Json(name = "avatar") val avatar: String?,
    @field:Json(name = "email") val email: String?,
    @field:Json(name = "full_name") val fullName: String?,
    @field:Json(name = "mobile") val mobile: String?,
    @field:Json(name = "role") val role: String?,
    @field:Json(name = "role_id") val roleId: String?,
    @field:Json(name = "status") val status: String?,
    @field:Json(name = "status_id") val statusId: String?,
    @field:Json(name = "token") val token: String?,
    @field:Json(name = "uuid") val uuid: String?
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
    @field:Json(name = "email") val email: String
)