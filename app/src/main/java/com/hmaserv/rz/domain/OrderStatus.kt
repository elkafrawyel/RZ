package com.hmaserv.rz.domain

import com.squareup.moshi.Json

data class OrderStatusResponse(
    @field:Json(name = "uuid") val uuid: String?,
    @field:Json(name = "title") val title: String?
)

sealed class OrderStatus(val name: String, val uuid: String) {
    data class Pending(val nameValue: String, val uuidValue: String) : OrderStatus(nameValue, uuidValue)
    data class Accepted(val nameValue: String, val uuidValue: String) : OrderStatus(nameValue, uuidValue)
    data class Refused(val nameValue: String, val uuidValue: String) : OrderStatus(nameValue, uuidValue)
    data class Deposit(val nameValue: String, val uuidValue: String) : OrderStatus(nameValue, uuidValue)
    data class Canceled(val nameValue: String, val uuidValue: String) : OrderStatus(nameValue, uuidValue)
    data class Completed(val nameValue: String, val uuidValue: String) : OrderStatus(nameValue, uuidValue)
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
            else -> null
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