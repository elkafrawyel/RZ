package com.hmaserv.rz.domain

import com.squareup.moshi.Json

data class ReviewResponse(
    @field:Json(name = "adUuid") val adUuid: String?
)

data class ReviewsRequest(
    @field:Json(name = "adUuid")
    val adUuid: String
)

data class Review(
    val adUuid: String,
    val avatar: String,
    val name: String,
    val rate: String,
    val date: String,
    val content: String
)

fun ReviewResponse.toReview(): Review? {
    return Review(
        adUuid!!,
        "",
        "mahmoud",
        "5",
        "10/01/2019",
        "new Comment"
    )
}

data class WriteReviewResponse(
    val sent: Boolean
)

data class WriteReviewRequest(
    @field:Json(name = "ad_uuid")
    val adUuid: String,
    @field:Json(name = "rate")
    val rate: Int,
    @field:Json(name = "comment")
    val comment: String
)