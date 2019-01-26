package com.hmaserv.rz.domain

import com.squareup.moshi.Json

data class OrderStatusResponse(
    @field:Json(name = "uuid") val uuid: String?,
    @field:Json(name = "title") val title: String?
)

sealed class OrderStatus {
    data class Unknown(val name: String = "UNKNOWN") : OrderStatus()
    data class Pending(val name: String = "PENDING", val uuid: String) : OrderStatus()
    data class Accepted(val name: String = "ACCEPT", val uuid: String) : OrderStatus()
    data class Refused(val name: String = "REFUSED", val uuid: String) : OrderStatus()
    data class Deposit(val name: String = "DEPOSIT", val uuid: String) : OrderStatus()
    data class Canceled(val name: String = "CANCELED", val uuid: String) : OrderStatus()
    data class Completed(val name: String = "COMPLETED", val uuid: String) : OrderStatus()
}

fun OrderStatusResponse.toOrderStatus(): OrderStatus? {
    if (uuid != null
        && title != null) {
        return when(title) {
            OrderStatusValues.PENDING.name -> OrderStatus.Pending(title, uuid)
            OrderStatusValues.ACCEPT.name -> OrderStatus.Accepted(title, uuid)
            OrderStatusValues.REFUSED.name -> OrderStatus.Refused(title, uuid)
            OrderStatusValues.DEPOSIT.name -> OrderStatus.Deposit(title, uuid)
            OrderStatusValues.CANCELED.name -> OrderStatus.Canceled(title, uuid)
            OrderStatusValues.COMPLETED.name -> OrderStatus.Completed(title, uuid)
            else -> OrderStatus.Unknown()
        }
    }

    return null
}

enum class OrderStatusValues {
    PENDING,
    ACCEPT,
    REFUSED,
    DEPOSIT,
    CANCELED,
    COMPLETED
}