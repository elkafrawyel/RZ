package store.rz.app.domain

import com.squareup.moshi.Json

data class FirebaseTokenRequest(
    @field:Json(name = "token") val token: String
)