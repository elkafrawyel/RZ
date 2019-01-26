package com.hmaserv.rz.domain

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
    val note: String?
)

data class OrderRequest(
    @field:Json(name = "order_uuid") val orderUuid: String
)

data class Order(
    val status: String,
    val price: Int,
    val amount: Int,
    val remaining: Int,
    val createdAt: String,
    val note: String
)

fun OrderResponse.toOrder(): Order? {
    if (status != null
        && orderPrice != null
        && amount != null
        && remaining != null
        && createdAt != null
    ) {
        return Order(
            status,
            orderPrice,
            amount,
            remaining,
            createdAt,
            note ?: ""
        )
    }

    return null
}