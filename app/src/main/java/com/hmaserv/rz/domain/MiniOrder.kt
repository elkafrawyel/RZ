package com.hmaserv.rz.domain

import com.squareup.moshi.Json

data class CreateOrderRequest(
    @field:Json(name = "ad_uuid") val adUuid: String,
    @field:Json(name = "contacts") val contact: OrderContact,
    @field:Json(name = "characteristics") val attributes: List<Attribute.MainAttribute>,
    @field:Json(name = "payment") val payment: String
)

data class OrderContactResponse(
    @field:Json(name = "city") val city: String?,
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "notes") val notes: String?,
    @field:Json(name = "mobile") val mobile: String?,
    @field:Json(name = "address") val address: String?
)

data class MiniOrderResponse(
    @field:Json(name = "order_uuid") val uuid: String?,
    @field:Json(name = "contacts") val contact: OrderContactResponse?,
    @field:Json(name = "status") val status: String?,
    @field:Json(name = "ad") val miniAd: MiniAdResponse?
)

data class MiniOrder(
    val uuid: String,
    val contact: OrderContact,
    val status: String,
    val miniAd: MiniAdResponse
)

data class OrderContact(
    val name: String,
    val address: String,
    val city: String,
    val mobile: String,
    val notes: String
)

enum class Payment(val value: String) {
    CASH("cash"),
    PAYPAL("paypal")
}

fun MiniOrderResponse.toMiniOrder(): MiniOrder? {
    val orderContact = contact?.toOrderContact()
    if (uuid != null
        && orderContact != null
        && status != null
        && miniAd != null
    ) {
        return MiniOrder(
            uuid,
            orderContact,
            status,
            miniAd
        )
    }

    return null
}

fun OrderContactResponse.toOrderContact(): OrderContact? {
    if (name != null
        && address != null
        && city != null
        && mobile != null
        && notes != null
    ) {
        return OrderContact(
            name,
            address,
            city,
            mobile,
            notes
        )
    }

    return null
}