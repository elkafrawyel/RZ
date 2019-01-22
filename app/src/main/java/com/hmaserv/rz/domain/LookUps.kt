package com.hmaserv.rz.domain

import com.squareup.moshi.Json


data class CityResponse(
    @field:Json(name = "uuid")
    val uuid: String?,
    @field:Json(name = "title")
    val title: String?
)

data class City(
    val uuid: String,
    val title: String
)

fun CityResponse.toCity(): City? {
    if (uuid != null
        && title != null
    ) {
        return City(
            uuid,
            title
        )
    }

    return null
}