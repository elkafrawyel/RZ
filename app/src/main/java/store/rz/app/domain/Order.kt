package store.rz.app.domain

import com.squareup.moshi.Json

data class OrderResponse(
    @field:Json(name = "status")
    val status: String?,
    @field:Json(name = "order_price")
    val orderPrice: Int?,
    @field:Json(name = "amount")
    val amount: Int?,
    @field:Json(name = "remaining")
    val remaining: Int?,
    @field:Json(name = "created_at")
    val createdAt: String?,
    @field:Json(name = "note")
    val note: String?,
    @field:Json(name = "payment")
    val paymentString: String?
)

data class OrderRequest(
    @field:Json(name = "order_uuid") val orderUuid: String
)

data class Order(
    val status: String,
    val orderStatus: OrderStatus,
    val price: Int,
    val amount: Int,
    val remaining: Int,
    val createdAt: String,
    val note: String,
    val payment: Payment
)

fun OrderResponse.toOrder(): Order? {
    val payment = paymentString?.toPayment()

    if (status != null
        && orderPrice != null
        && amount != null
        && remaining != null
        && createdAt != null
        && payment != null
    ) {
        return Order(
            status,
            OrderStatus.Unknown(),
            orderPrice,
            amount,
            remaining,
            createdAt,
            note ?: "",
            payment
        )
    }

    return null
}