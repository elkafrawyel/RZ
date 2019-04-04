package store.rz.app.domain

import store.rz.app.utils.Constants
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
    @field:Json(name = "role_id") val roleId: Int?,
    @field:Json(name = "status") val status: String?,
    @field:Json(name = "status_id") val statusId: Int?,
    @field:Json(name = "token") val token: String?,
    @field:Json(name = "uuid") val uuid: String?,
    @field:Json(name = "acceptTerms") val acceptTerms:Boolean?

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

data class VerifyUserRequest(
    @field:Json(name = "code") val code: Int
)

data class ForgetPassword(
    @field:Json(name = "mobile") val email: String
)

data class UpgradeUserRequest(
    @field:Json(name = "role_uuid") val fullName: String
)