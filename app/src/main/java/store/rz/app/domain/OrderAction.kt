package store.rz.app.domain
import com.squareup.moshi.Json


data class OrderActionRequest(
    @field:Json(name = "order_uuid")
    val orderUuid: String,
    @field:Json(name = "action")
    val action: String,
    @field:Json(name = "amount")
    val amount: Int = 0,
    @field:Json(name = "note")
    val note: String? = null
)

data class OrderActionResponse(
    @field:Json(name = "order_uuid")
    val orderUuid: String?,
    @field:Json(name = "action_uuid")
    val actionUuid: String?,
    @field:Json(name = "amount")
    val amount: Int?,
    @field:Json(name = "note")
    val note: String?
)