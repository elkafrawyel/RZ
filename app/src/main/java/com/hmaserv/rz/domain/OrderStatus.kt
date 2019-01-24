package com.hmaserv.rz.domain

import com.squareup.moshi.Json

data class OrderStatusResponse(
    @field:Json(name = "uuid") val uuid: String?,
    @field:Json(name = "title") val title: String?
)

fun OrderStatusResponse.toOrderStatus(): OrderStatus? {
    if (uuid != null
        && title != null) {
        return when(title) {
            "PENDING" -> OrderStatus.Pending(title, uuid)
            "ACCEPT" -> OrderStatus.Pending(title, uuid)
            "REFUSED" -> OrderStatus.Pending(title, uuid)
            "DEPOSIT" -> OrderStatus.Pending(title, uuid)
            "COMPLETED" -> OrderStatus.Pending(title, uuid)
            else -> null
        }
    }

    return null
}